/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.analysis;

import org.antlr.v5.tool.ast.AltAST;
import org.antlr.v5.tool.ast.GrammarAST;
import org.antlr.v5.tool.ast.RuleRefAST;

import java.util.HashSet;

public class RuleAltInfo {
	public int number; // original alt index (from 1)
	public AltAST ast;
	public String label;
	public AltType type = AltType.other;
	public boolean nonConformingLeftRecursion = false;
	public AssocType assocType = AssocType.left;
	public HashSet<RuleRefAST> leftRecursiveLeftmostRuleRefs = new HashSet<>();
	public HashSet<RuleRefAST> leftRecursiveRightmostRuleRefs = new HashSet<>();
	public GrammarAST leftRecursiveRuleRefLabel;

	public boolean isLeftRecursive() {
		return type == AltType.suffixLR || type == AltType.binaryLR;
	}
}
