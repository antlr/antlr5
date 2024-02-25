/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.automata.optimization;

import org.antlr.v5.runtime.core.misc.Interval;
import org.antlr.v5.runtime.core.misc.IntervalSet;
import org.antlr.v5.runtime.core.state.ATNState;
import org.antlr.v5.runtime.core.state.BasicState;
import org.antlr.v5.runtime.core.state.BlockEndState;
import org.antlr.v5.runtime.core.state.DecisionState;
import org.antlr.v5.runtime.core.transition.*;
import org.antlr.v5.tool.ErrorType;
import org.antlr.v5.tool.Rule;

import java.util.List;

public class SetMerger {
	public final ATNOptimizerHelper helper;

	public static int optimize(ATNOptimizerHelper helper) {
		List<ATNState> removedStates = helper.getRemovedStates();
		int numberOfRemovedStates = removedStates.size();
		new SetMerger(helper).optimize();
		return removedStates.size() - numberOfRemovedStates;
	}

	public SetMerger(ATNOptimizerHelper helper) {
		this.helper = helper;
	}

	private void optimize() {
		List<DecisionState> decisions = helper.atn.getDecisionToState();
		for (DecisionState decision : decisions) {
			Rule rule = null;
			if (decision.getRuleIndex() >= 0) {
				rule = helper.grammar.getRule(decision.getRuleIndex());
				if (Character.isLowerCase(rule.name.charAt(0))) {
					// The optimizer currently doesn't support parser's ATN
					continue;
				}
			}

			IntervalSet setTransitions = new IntervalSet();
			for (int i = 0; i < decision.getNumberOfTransitions(); i++) {
				Transition epsTransition = decision.transition(i);
				if (!(epsTransition instanceof EpsilonTransition)) {
					continue;
				}

				ATNState epsTarget = ((EpsilonTransition) epsTransition).getTarget();

				if (helper.getInTransitions(epsTarget).size() != 1 ||
						!(epsTarget instanceof BasicState) ||
						epsTarget.getNumberOfTransitions() != 1)
				{
					continue;
				}

				Transition transition = ((EpsilonTransition) epsTransition).getTarget().transition(0);
				if (!(transition.getTarget() instanceof BlockEndState)) {
					continue;
				}

				if (transition instanceof NotSetTransition) {
					// TODO: not yet implemented
					continue;
				}

				if (transition instanceof AtomTransition
						|| transition instanceof RangeTransition
						|| transition instanceof SetTransition)
				{
					setTransitions.add(i);
				}
			}

			// due to min alt resolution policies, can only collapse sequential alts
			for (int i = setTransitions.getIntervals().size() - 1; i >= 0; i--) {
				Interval interval = setTransitions.getIntervals().get(i);
				if (interval.length() <= 1) {
					continue;
				}

				ATNState blockEndState = decision.transition(interval.getA()).getTarget().transition(0).getTarget();
				IntervalSet matchSet = new IntervalSet();
				for (int j = interval.getA(); j <= interval.getB(); j++) {
					Transition matchTransition = decision.transition(j).getTarget().transition(0);
					if (matchTransition instanceof NotSetTransition) {
						throw new UnsupportedOperationException("Not yet implemented.");
					}
					IntervalSet set =  matchTransition.label();
					IntervalSet intersection = matchSet.and(set);
					if (!intersection.isNil()) {
						helper.grammar.tool.errMgr.grammarError(ErrorType.CHARACTERS_COLLISION_IN_SET, helper.grammar.fileName,
								rule != null ? rule.ast.token : null, intersection.toString(true),
								matchSet.toString(true));
					}

					matchSet.addAll(set);
				}

				Transition newTransition;
				if (matchSet.getIntervals().size() == 1) {
					Interval matchInterval = matchSet.getIntervals().get(0);
					newTransition = CodePointTransitions.INSTANCE.createWithCodePointRange(blockEndState, matchInterval.getA(), matchInterval.getB());
				}
				else {
					newTransition = new SetTransition(blockEndState, matchSet);
				}

				ATNState newTarget = decision.transition(interval.getA()).getTarget();
				helper.replaceTransition(newTarget, newTarget.transition(0), newTransition);

				for (int j = interval.getA() + 1; j <= interval.getB(); j++) {
					ATNState target = decision.transition(interval.getA() + 1).getTarget();
					helper.removeInOutTransitions(target);
					helper.addReplacement(target, newTarget);
				}
			}
		}
	}
}
