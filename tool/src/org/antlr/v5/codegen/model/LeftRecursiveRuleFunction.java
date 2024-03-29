/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model;

import org.antlr.v5.codegen.CodeGenerator;
import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.codegen.model.decl.RuleContextDecl;
import org.antlr.v5.codegen.model.decl.RuleContextListDecl;
import org.antlr.v5.codegen.model.decl.StructDecl;
import org.antlr.v5.parse.ANTLRParser;
import kotlin.Pair;
import org.antlr.v5.tool.LeftRecursiveRule;
import org.antlr.v5.tool.Rule;
import org.antlr.v5.tool.ast.GrammarAST;

public class LeftRecursiveRuleFunction extends RuleFunction {
	public LeftRecursiveRuleFunction(OutputModelFactory factory, LeftRecursiveRule r) {
		super(factory, r);

		CodeGenerator gen = factory.getGenerator();
		// Since we delete x=lr, we have to manually add decls for all labels
		// on left-recur refs to proper structs
		for (Pair<GrammarAST,String> pair : r.leftRecursiveRuleRefLabels) {
			GrammarAST idAST = pair.getFirst();
			String altLabel = pair.getSecond();
			String label = idAST.getText();
			GrammarAST rrefAST = (GrammarAST)idAST.getParent().getChild(1);
			if ( rrefAST.getType() == ANTLRParser.RULE_REF ) {
				Rule targetRule = factory.getGrammar().getRule(rrefAST.getText());
				String ctxName = gen.getTarget().getRuleFunctionContextStructName(targetRule);
				RuleContextDecl d;
				if (idAST.getParent().getType() == ANTLRParser.ASSIGN) {
					d = new RuleContextDecl(factory, label, ctxName);
				}
				else {
					d = new RuleContextListDecl(factory, label, ctxName);
				}

				StructDecl struct = ruleCtx;
				if ( altLabelCtxs!=null ) {
					StructDecl s = altLabelCtxs.get(altLabel);
					if ( s!=null ) struct = s; // if alt label, use subctx
				}
				struct.addDecl(d); // stick in overall rule's ctx
			}
		}
	}
}
