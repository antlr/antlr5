/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.automata;

import org.antlr.v5.runtime.core.atn.ATN;
import org.antlr.v5.runtime.core.misc.Interval;
import org.antlr.v5.runtime.core.misc.IntervalSet;
import org.antlr.v5.runtime.core.state.ATNState;
import org.antlr.v5.runtime.core.state.BlockEndState;
import org.antlr.v5.runtime.core.state.DecisionState;
import org.antlr.v5.runtime.core.transition.*;
import org.antlr.v5.tool.ErrorType;
import org.antlr.v5.tool.Grammar;
import org.antlr.v5.tool.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sam Harwell
 */
public class ATNOptimizer {

	public static void optimize(Grammar g, ATN atn) {
		optimizeSets(g, atn);
		optimizeStates(atn);
	}

	private static void optimizeSets(Grammar g, ATN atn) {
		if (g.isParser()) {
			// parser codegen doesn't currently support SetTransition
			return;
		}

		int removedStates = 0;
		List<DecisionState> decisions = atn.getDecisionToState();
		for (DecisionState decision : decisions) {
			Rule rule = null;
			if (decision.getRuleIndex() >= 0) {
				rule = g.getRule(decision.getRuleIndex());
				if (Character.isLowerCase(rule.name.charAt(0))) {
					// parser codegen doesn't currently support SetTransition
					continue;
				}
			}

			IntervalSet setTransitions = new IntervalSet();
			for (int i = 0; i < decision.getNumberOfTransitions(); i++) {
				Transition epsTransition = decision.transition(i);
				if (!(epsTransition instanceof EpsilonTransition)) {
					continue;
				}

				if (epsTransition.getTarget().getNumberOfTransitions() != 1) {
					continue;
				}

				Transition transition = epsTransition.getTarget().transition(0);
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
						g.tool.errMgr.grammarError(ErrorType.CHARACTERS_COLLISION_IN_SET, g.fileName,
							rule != null ? rule.ast.token : null, intersection.toString(true),
							matchSet.toString(true));
					}

					matchSet.addAll(set);
				}

				Transition newTransition;
				if (matchSet.getIntervals().size() == 1) {
					if (matchSet.size() == 1) {
						newTransition = CodePointTransitions.INSTANCE.createWithCodePoint(blockEndState, matchSet.getMinElement());
					}
					else {
						Interval matchInterval = matchSet.getIntervals().get(0);
						newTransition = CodePointTransitions.INSTANCE.createWithCodePointRange(blockEndState, matchInterval.getA(), matchInterval.getB());
					}
				}
				else {
					newTransition = new SetTransition(blockEndState, matchSet);
				}

				decision.transition(interval.getA()).getTarget().setTransition(0, newTransition);
				for (int j = interval.getA() + 1; j <= interval.getB(); j++) {
					Transition removed = decision.removeTransition(interval.getA() + 1);
					atn.removeState(removed.getTarget());
					removedStates++;
				}
			}
		}

//		System.out.println("ATN optimizer removed " + removedStates + " states by collapsing sets.");
	}

	private static void optimizeStates(ATN atn) {
//		System.out.println(atn.states);
		List<ATNState> compressed = new ArrayList<ATNState>();
		int i = 0; // new state number
		for (ATNState s : atn.getStates()) {
			if ( s!=null ) {
				compressed.add(s);
				s.setStateNumber(i); // reset state number as we shift to new position
				i++;
			}
		}
//		System.out.println(compressed);
//		System.out.println("ATN optimizer removed " + (atn.states.size() - compressed.size()) + " null states.");
		atn.getStates().clear();
		atn.getStates().addAll(compressed);
	}

	private ATNOptimizer() {
	}

}
