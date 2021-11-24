/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.analysis;

import org.antlr.runtime.tree.Tree;
import org.antlr.v5.Tool;
import org.antlr.v5.tool.ErrorType;
import org.antlr.v5.tool.ast.ActionAST;
import org.antlr.v5.tool.ast.AltAST;

import static org.antlr.v5.parse.ANTLRParser.*;
import static org.antlr.v5.parse.ANTLRParser.RULE_REF;

public class RuleAltInfoExtractor {
	public final Tool tool;
	public final String ruleName;

	public RuleAltInfoExtractor(Tool tool, String ruleName) {
		this.tool = tool;
		this.ruleName = ruleName;
	}

	public RuleAltInfo extract(AltAST altAst, int altNum) {
		RuleAltInfo result = new RuleAltInfo();
		result.ast = altAst;
		result.number = altNum;
		result.label = altAst.altLabel!=null ? altAst.altLabel.getText() : null;
		extract(result, altAst, true, true);
		return result;
	}

	private AltType extract(RuleAltInfo ruleAltInfo, Tree tree, boolean leftmost, boolean rightmost) {
		if (tree == null) return AltType.other;

		switch (tree.getType()) {
			// Just an alternative
			// e
			//     : e ID
			case ALT:
				Tree leftChild = tree.getChild(0);
				Tree rightChild = getLastNotActionAst(tree);
				AltType leftmostAltType = extract(ruleAltInfo, leftChild, leftmost, false);
				AltType rightmostAltType = extract(ruleAltInfo, rightChild, false, rightmost);
				boolean matchRightmostAlt = isMatchRule(rightmostAltType);
				if (isMatchRule(leftmostAltType)) {
					if (matchRightmostAlt) {
						if (leftChild == rightChild) {
							// a : a | 'B';
							ruleAltInfo.nonConformingLeftRecursion = true;
						}
						ruleAltInfo.type = AltType.binaryLR;
					} else {
						ruleAltInfo.type = leftmostAltType;
					}
				} else {
					ruleAltInfo.type = matchRightmostAlt ? rightmostAltType : AltType.other;
				}
				return ruleAltInfo.type;
			// All alternatives should be left recursive
			// e
			//     : (e '+' e | e '-' e)
			case BLOCK:
				for (int i = 0; i < tree.getChildCount(); i++) {
					// TODO
				}
				break;
			// Option
			// e
			//     : <assoc=right> e '+' e
			case ELEMENT_OPTIONS:
				AltType result = extract(ruleAltInfo, tree.getParent().getChild(1), leftmost, rightmost);
				String assocString = tree.getChild(0).getChild(1).getText();
				try {
					ruleAltInfo.assocType = AssocType.valueOf(assocString);
				} catch (IllegalArgumentException e) {
					AltAST altAst = ruleAltInfo.ast;
					tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION_VALUE,
							altAst.g.fileName, altAst.getOptionAST("assoc").getToken(), "assoc", assocString);
				}
				return result;
			// Label
			// e
			//     : l1=e '+' l2=e
			case ASSIGN:
			case PLUS_ASSIGN:
				return extract(ruleAltInfo, tree.getChild(1), leftmost, rightmost);
			case RULE_REF:
				Tree firstChild = tree.getChild(0);
				if (firstChild instanceof ActionAST) {
					// a : a[4] 'x' | ...
					ruleAltInfo.nonConformingLeftRecursion = true;
				}

				if (tree.getText().equals(ruleName)) {
					return leftmost
							? AltType.suffixLR
							: rightmost
							? AltType.prefix
							: AltType.other;
				} else {
					return AltType.other;
				}
		}

		return AltType.other;
	}

	private Tree getLastNotActionAst(Tree tree) {
		for (int i = tree.getChildCount() - 1; i >= 0; i--) {
			Tree child = tree.getChild(i);
			int type = child.getType();
			if (type != ACTION && type != SEMPRED) {
				return child;
			}
		}
		return null;
	}

	public static boolean isMatchRule(AltType altType) {
		return altType != null && altType != AltType.other;
	}
}
