/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.analysis;

import org.antlr.v5.runtime.core.atn.*;
import org.antlr.v5.runtime.core.state.*;
import org.antlr.v5.misc.OrderedHashSet;
import org.antlr.v5.runtime.core.transition.RuleTransition;
import org.antlr.v5.runtime.core.transition.Transition;
import org.antlr.v5.tool.ErrorType;
import org.antlr.v5.tool.Grammar;
import org.antlr.v5.tool.Rule;

import java.util.*;

public class LeftRecursionDetector {
	private final Grammar g;
	private final ATN atn;

	/** Holds a list of cycles (sets of rule names). */
	private final List<Set<Rule>> listOfRecursiveCycles = new ArrayList<>();

	/** Which rule start states have we visited while looking for a single
	 * 	left-recursion check?
	 */
	private final Set<RuleStartState> rulesVisitedPerRuleCheck = new HashSet<>();

	public boolean containsErrors() {
		return !listOfRecursiveCycles.isEmpty();
	}

	public LeftRecursionDetector(Grammar g, ATN atn) {
		this.g = g;
		this.atn = atn;
	}

	public void check() {
		for (RuleStartState start : atn.getRuleToStartState()) {
			rulesVisitedPerRuleCheck.clear();
			rulesVisitedPerRuleCheck.add(start);
			check(g.getRule(start.getRuleIndex()), start, new HashSet<>());
		}

		for (Collection<Rule> cycle : listOfRecursiveCycles) {
			for (Rule rule : cycle) {
				g.tool.errMgr.grammarError(ErrorType.LEFT_RECURSION_CYCLES, g.fileName, rule.ast.token, cycle);
			}
		}
	}

	/** From state s, look for any transition to a rule that is currently
	 *  being traced.  When tracing r, visitedPerRuleCheck has r
	 *  initially.  If you reach a rule stop state, return but notify the
	 *  invoking rule that the called rule is nullable. This implies that
	 *  invoking rule must look at follow transition for that invoking state.
	 *
	 *  The visitedStates tracks visited states within a single rule so
	 *  we can avoid epsilon-loop-induced infinite recursion here.  Keep
	 *  filling the cycles in listOfRecursiveCycles and also, as a
	 *  side-effect, set leftRecursiveRules.
	 */
	public boolean check(Rule enclosingRule, ATNState s, Set<ATNState> visitedStates) {
		if ( s instanceof RuleStopState) return true;
		if ( !visitedStates.add(s) ) return false;

		//System.out.println("visit "+s);
		int n = s.getNumberOfTransitions();
		boolean stateReachesStopState = false;
		for (int i=0; i<n; i++) {
			Transition t = s.transition(i);
			if ( t instanceof RuleTransition) {
				RuleTransition rt = (RuleTransition) t;
				Rule r = g.getRule(rt.getRuleIndex());
				if ( rulesVisitedPerRuleCheck.contains((RuleStartState)t.getTarget()) ) {
					addRulesToCycle(enclosingRule, r);
				}
				else {
					// must visit if not already visited; mark target, pop when done
					rulesVisitedPerRuleCheck.add((RuleStartState)t.getTarget());
					// send new visitedStates set per rule invocation
					boolean nullable = check(r, t.getTarget(), new HashSet<ATNState>());
					// we're back from visiting that rule
					rulesVisitedPerRuleCheck.remove((RuleStartState)t.getTarget());
					if ( nullable ) {
						stateReachesStopState |= check(enclosingRule, rt.getFollowState(), visitedStates);
					}
				}
			}
			else if ( t.isEpsilon() ) {
				stateReachesStopState |= check(enclosingRule, t.getTarget(), visitedStates);
			}
			// else ignore non-epsilon transitions
		}
		return stateReachesStopState;
	}

	/** enclosingRule calls targetRule. Find the cycle containing
	 *  the target and add the caller.  Find the cycle containing the caller
	 *  and add the target.  If no cycles contain either, then create a new
	 *  cycle.
	 */
	protected void addRulesToCycle(Rule enclosingRule, Rule targetRule) {
		//System.err.println("left-recursion to "+targetRule.name+" from "+enclosingRule.name);
		boolean foundCycle = false;
		for (Set<Rule> rulesInCycle : listOfRecursiveCycles) {
			// ensure both rules are in same cycle
			if (rulesInCycle.contains(targetRule)) {
				rulesInCycle.add(enclosingRule);
				foundCycle = true;
			}
			if (rulesInCycle.contains(enclosingRule)) {
				rulesInCycle.add(targetRule);
				foundCycle = true;
			}
		}
		if ( !foundCycle ) {
			Set<Rule> cycle = new OrderedHashSet<Rule>();
			cycle.add(targetRule);
			cycle.add(enclosingRule);
			listOfRecursiveCycles.add(cycle);
		}
	}
}
