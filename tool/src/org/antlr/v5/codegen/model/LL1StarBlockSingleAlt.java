/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.runtime.core.misc.IntervalSet;
import org.antlr.v5.runtime.core.state.StarLoopEntryState;
import org.antlr.v5.tool.ast.GrammarAST;

import java.util.List;

/** */
public class LL1StarBlockSingleAlt extends LL1Loop {
	public LL1StarBlockSingleAlt(OutputModelFactory factory, GrammarAST starRoot, List<CodeBlockForAlt> alts) {
		super(factory, starRoot, alts);

		StarLoopEntryState star = (StarLoopEntryState)starRoot.atnState;
		loopBackStateNumber = star.getLoopBackState().getStateNumber();
		this.decision = star.getDecision();
		IntervalSet[] altLookSets = factory.getGrammar().decisionLOOK.get(decision);
		assert altLookSets.length == 2;
		IntervalSet enterLook = altLookSets[0];
		IntervalSet exitLook = altLookSets[1];
		loopExpr = addCodeForLoopLookaheadTempVar(enterLook);
	}
}
