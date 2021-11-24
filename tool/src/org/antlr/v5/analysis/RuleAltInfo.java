/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.analysis;

import org.antlr.v5.tool.ast.AltAST;

public class RuleAltInfo {
	public int number; // original alt index (from 1)
	public AltAST ast;
	public String label;
	public AltType type = AltType.other;
	public boolean nonConformingLeftRecursion = false;
	public AssocType assocType = AssocType.left;

	public RuleAltInfo() {
	}

	public boolean isLeftRecursive() {
		return type == AltType.suffixLR || type == AltType.binaryLR;
	}
}
