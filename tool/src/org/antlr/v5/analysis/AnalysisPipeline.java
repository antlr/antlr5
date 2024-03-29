/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.analysis;

import org.antlr.v5.misc.Utils;
import org.antlr.v5.runtime.core.Token;
import org.antlr.v5.runtime.core.state.DecisionState;
import org.antlr.v5.runtime.core.atn.LL1Analyzer;
import org.antlr.v5.runtime.core.misc.IntervalSet;
import org.antlr.v5.tool.ErrorType;
import org.antlr.v5.tool.Grammar;
import org.antlr.v5.tool.Rule;
import org.antlr.v5.tool.ast.GrammarAST;

import java.util.ArrayList;
import java.util.Arrays;

public class AnalysisPipeline {
	public Grammar g;

	public AnalysisPipeline(Grammar g) {
		this.g = g;
	}

	public void process() {
		// LEFT-RECURSION CHECK
		LeftRecursionDetector lr = new LeftRecursionDetector(g, g.atn);
		lr.check();
		if ( lr.containsErrors() ) return; // bail out

		if (g.isLexer()) {
			processLexer();
		}
		else {
			// BUILD DFA FOR EACH DECISION
			processParser();
		}
	}

	protected void processLexer() {
		// make sure all non-fragment lexer rules must match at least one symbol
		for (Rule rule : g.rules.values()) {
			if (rule.isFragment()) {
				continue;
			}

			LL1Analyzer analyzer = new LL1Analyzer(g.atn);
			IntervalSet look = analyzer.LOOK(g.atn.getRuleToStartState()[rule.index], null);
			if (look.contains(Token.EPSILON)) {
				g.tool.errMgr.grammarError(ErrorType.EPSILON_TOKEN, g.fileName, ((GrammarAST)rule.ast.getChild(0)).getToken(), rule.name);
			}
		}
	}

	protected void processParser() {
		g.decisionLOOK = new ArrayList<IntervalSet[]>(g.atn.getNumberOfDecisions()+1);
		for (DecisionState s : g.atn.getDecisionToState()) {
            g.tool.log("LL1", "\nDECISION "+ s.getDecision() +" in rule "+g.getRule(s.getRuleIndex()).name);
			IntervalSet[] look;
			if ( s.getNonGreedy() ) { // nongreedy decisions can't be LL(1)
				look = new IntervalSet[s.getNumberOfTransitions() + 1];
			}
			else {
				LL1Analyzer anal = new LL1Analyzer(g.atn);
				look = anal.getDecisionLookahead(s);
				g.tool.log("LL1", "look=" + Arrays.toString(look));
			}

			assert s.getDecision() + 1 >= g.decisionLOOK.size();
			Utils.setSize(g.decisionLOOK, s.getDecision() + 1);
			g.decisionLOOK.set(s.getDecision(), look);
			g.tool.log("LL1", "LL(1)? " + disjoint(look));
		}
	}

	/** Return whether lookahead sets are disjoint; no lookahead ⇒ not disjoint */
	public static boolean disjoint(IntervalSet[] altLook) {
		boolean collision = false;
		IntervalSet combined = new IntervalSet();
		if ( altLook==null ) return false;
		for (IntervalSet look : altLook) {
			if ( look==null ) return false; // lookahead must've computation failed
			if ( !look.and(combined).isNil() ) {
				collision = true;
				break;
			}
			combined.addAll(look);
		}
		return !collision;
	}
}
