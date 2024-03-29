/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.runtime.core.state.DecisionState;
import org.antlr.v5.runtime.core.misc.IntervalSet;
import org.antlr.v5.tool.ast.GrammarAST;

import java.util.List;

/** (A | B | C) */
public class LL1AltBlock extends LL1Choice {
	public LL1AltBlock(OutputModelFactory factory, GrammarAST blkAST, List<CodeBlockForAlt> alts) {
		super(factory, blkAST, alts);
		this.decision = ((DecisionState) blkAST.atnState).getDecision();

		/** Lookahead for each alt 1..n */
		IntervalSet[] altLookSets = factory.getGrammar().decisionLOOK.get(decision);
		altLook = getAltLookaheadAsStringLists(altLookSets);

		IntervalSet expecting = IntervalSet.Companion.or(altLookSets); // combine alt sets
		this.error = getThrowNoViableAlt(factory, blkAST, expecting);
	}
}
