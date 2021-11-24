/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.analysis;

import org.antlr.runtime.tree.Tree;
import org.antlr.v5.Tool;
import org.antlr.v5.tool.ast.AltAST;
import org.antlr.v5.tool.ast.GrammarAST;
import org.antlr.v5.tool.ast.RuleAST;

import java.util.ArrayList;
import java.util.List;

import static org.antlr.v5.parse.GrammarTreeVisitor.BLOCK;
import static org.antlr.v5.parse.GrammarTreeVisitor.RETURNS;

public class RuleInfo {
	public final List<RuleAltInfo> ruleAltInfos;
	public final boolean isLeftRecursive;
	public final boolean nonConformingLeftRecursion;
	public final GrammarAST retvals;

	public RuleInfo(List<RuleAltInfo> ruleAltInfos, GrammarAST retvals, boolean leftRecursive, boolean nonConformingLeftRecursion) {
		this.ruleAltInfos = ruleAltInfos;
		this.isLeftRecursive = leftRecursive;
		this.nonConformingLeftRecursion = nonConformingLeftRecursion;
		this.retvals = retvals;
	}

	/**
	 * Match (RULE RULE_REF (BLOCK (ALT .*) (ALT RULE_REF[self] .*) (ALT .*)))
	 * Match (RULE RULE_REF (BLOCK (ALT .*) (ALT (ASSIGN ID RULE_REF[self]) .*) (ALT .*)))
	 */
	public static RuleInfo collectRuleInfo(Tool tool, RuleAST ruleAST) {
		List<RuleAltInfo> ruleAltInfos = new ArrayList<>();
		if ( ruleAST==null ) return new RuleInfo(ruleAltInfos, null, false, false);
		GrammarAST block = (GrammarAST)ruleAST.getFirstChildWithType(BLOCK);
		if ( block==null ) return new RuleInfo(ruleAltInfos, null, false, false);

		GrammarAST retvals = null;
		Tree returns = ruleAST.getFirstChildWithType(RETURNS);
		if (returns != null) {
			retvals = (GrammarAST)returns.getChild(0);
		}

		List<?> children = block.getChildren();
		int n = children.size();
		RuleAltInfoExtractor ruleAltInfoExtractor = new RuleAltInfoExtractor(tool, ruleAST.getRuleName());
		boolean isLeftRecursive = false;
		boolean nonConformingLeftRecursion = false;
		for (int i = 0; i < n; i++) {
			RuleAltInfo ruleAltInfo = ruleAltInfoExtractor.extract((AltAST) children.get(i), i + 1);
			ruleAltInfos.add(ruleAltInfo);
			if (ruleAltInfo.isLeftRecursive()) {
				isLeftRecursive = true;
				if (ruleAltInfo.nonConformingLeftRecursion) {
					nonConformingLeftRecursion = true;
				}
			}
		}
		return new RuleInfo(ruleAltInfos, retvals, isLeftRecursive, nonConformingLeftRecursion);
	}
}
