/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.automata.optimization;

import org.antlr.v5.runtime.core.atn.ATN;
import org.antlr.v5.runtime.core.state.ATNState;
import org.antlr.v5.runtime.core.state.BasicState;
import org.antlr.v5.runtime.core.state.RuleStartState;
import org.antlr.v5.runtime.core.transition.*;

import java.util.*;

public class RedundantEpsilonRemover {
	public final ATN atn;

	private final ATNOptimizerHelper helper;

	private RedundantEpsilonRemover(ATN atn, ATNOptimizerHelper helper) {
		this.atn = atn;
		this.helper = helper;
	}

	public static int optimize(ATNOptimizerHelper helper) {
		List<ATNState> removedStates = helper.getRemovedStates();
		int numberOfRemovedStates = removedStates.size();
		new RedundantEpsilonRemover(helper.atn, helper).traverseATN();
		return removedStates.size() - numberOfRemovedStates;
	}

	private void traverseATN() {
		Set<ATNState> visitedStates = new HashSet<>();
		for (RuleStartState start : atn.getRuleToStartState()) {
			traverseState(start, visitedStates);
		}
	}

	private void traverseState(ATNState state, Set<ATNState> visitedStates) {
		if (!visitedStates.add(state)) {
			return;
		}

		optimizeState(state);

		for (int i = 0; i < state.getNumberOfTransitions(); i++) {
			Transition transition = state.transition(i);
			traverseState(transition.getTarget(), visitedStates);
			if (transition instanceof RuleTransition) {
				traverseState(((RuleTransition) transition).getFollowState(), visitedStates);
			}
		}
	}

	private void optimizeState(ATNState state) {
		if (state instanceof BasicState && state.getNumberOfTransitions() == 1) {
			Transition outTransition = state.transition(0);
			Boolean removeInTransition = null;

			List<InTransition> inTransitions = helper.getInTransitions(state);
			InTransition inTransition0 = inTransitions.get(0);

			if (inTransitions.size() == 1) {
				// Input:
				//     ε     t
				// I1 --> S --> O1

				// Output:
				//     t
				// I1 --> O1
				if (isSafeForRemovingEpsilon(inTransition0.transition) && validateInTransitionForRemoving(inTransition0, outTransition)) {
					helper.removeInTransitionsForOutStates(state);
					helper.replaceTransition(inTransition0, outTransition);
					removeInTransition = true;
				}
			}

			if (removeInTransition == null && isSafeForRemovingEpsilon(outTransition)) {
				// State with several incoming transitions of any kind can be safely removed

				// Input:
				// I1
				//    \     ε
				// I2 -> S --> O1
				//    /
				// I3

				// Output:
				// I1
				//    \
				// I2 -> O1
				//    /
				// I3
				helper.removeInTransitionsForOutStates(state);
				for (InTransition inTransition : inTransitions) {
					Transition newTransition = cloneTransition(inTransition, outTransition.getTarget());
					helper.replaceTransition(inTransition, newTransition);
				}
				removeInTransition = false;
			}

			if (removeInTransition != null) {
				ATNState newState;
				if (removeInTransition) {
					newState = inTransition0.previousState;
				} else {
					newState = outTransition.getTarget();
				}

				helper.addReplacement(state, newState);
			}
		}
	}

	private static boolean isSafeForRemovingEpsilon(Transition transition) {
		return transition instanceof EpsilonTransition && ((EpsilonTransition) transition).getOutermostPrecedenceReturn() == -1;
	}

	private static boolean validateInTransitionForRemoving(InTransition inTransition, Transition outTransition) {
		Boolean onlyEpsilonTransitions = null;
		ATNState prevState = inTransition.previousState;
		Transition transitionToRemove = inTransition.transition;
		for (int i = 0; i < prevState.getNumberOfTransitions(); i++) {
			Transition t = prevState.transition(i);
			if (t != transitionToRemove) {
				if (!t.isEpsilon()) {
					onlyEpsilonTransitions = false;
					break;
				}
				onlyEpsilonTransitions = true;
			}
		}

		if (onlyEpsilonTransitions != null && onlyEpsilonTransitions) {
			return outTransition instanceof EpsilonTransition;
		}
		else {
			return true;
		}
	}

	private static Transition cloneTransition(InTransition inTransition, ATNState newTarget) {
		Transition transition = inTransition.transition;
		switch (transition.getSerializationType()) {
			case Transition.EPSILON:
				return new EpsilonTransition(newTarget, ((EpsilonTransition) transition).getOutermostPrecedenceReturn());
			case Transition.RANGE:
				RangeTransition rangeTransition = (RangeTransition) transition;
				return new RangeTransition(newTarget, rangeTransition.getFrom(), rangeTransition.getTo());
			case Transition.RULE:
				RuleTransition ruleTransition = (RuleTransition) transition;
				RuleStartState ruleStart;
				ATNState followState;
				if (inTransition.isFollowState) {
					ruleStart = (RuleStartState) ruleTransition.getTarget();
					followState = newTarget;
				}
				else {
					ruleStart = (RuleStartState) newTarget;
					followState = ruleTransition.getFollowState();
				}
				return new RuleTransition(ruleStart, ruleTransition.getRuleIndex(), ruleTransition.getPrecedence(), followState);
			case Transition.PREDICATE:
				PredicateTransition predicateTransition = (PredicateTransition) transition;
				return new PredicateTransition(newTarget, predicateTransition.getRuleIndex(),
                        predicateTransition.getPredIndex(), predicateTransition.isCtxDependent());
			case Transition.PRECEDENCE:
				return new PrecedencePredicateTransition(newTarget, ((PrecedencePredicateTransition) transition).getPrecedence());
			case Transition.ATOM:
				return new AtomTransition(newTarget, ((AtomTransition) transition).getLabel());
			case Transition.ACTION:
				ActionTransition actionTransition = (ActionTransition) transition;
				return new ActionTransition(newTarget, actionTransition.getRuleIndex(), actionTransition.getActionIndex(),
                        actionTransition.isCtxDependent());
			case Transition.SET:
				return new SetTransition(newTarget, ((SetTransition) transition).getSet());
			case Transition.NOT_SET:
				return new NotSetTransition(newTarget, ((NotSetTransition) transition).getSet());
			case Transition.WILDCARD:
				return new WildcardTransition(newTarget);
		}
		assert true : "Transition should exist";
		return null;
	}
}
