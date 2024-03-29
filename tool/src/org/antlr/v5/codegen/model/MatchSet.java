/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.codegen.model.decl.Decl;
import org.antlr.v5.codegen.model.decl.TokenTypeDecl;
import org.antlr.v5.runtime.core.transition.SetTransition;
import org.antlr.v5.tool.ast.GrammarAST;

public class MatchSet extends MatchToken {
	@ModelElement public TestSetInline expr;
	@ModelElement public CaptureNextTokenType capture;

	public MatchSet(OutputModelFactory factory, GrammarAST ast) {
		super(factory, ast);
		SetTransition st = (SetTransition)ast.atnState.transition(0);
		int wordSize = factory.getGenerator().getTarget().getInlineTestSetWordSize();
		expr = new TestSetInline(factory, null, st.getSet(), wordSize);
		Decl d = new TokenTypeDecl(factory, expr.varName);
		factory.getCurrentRuleFunction().addLocalDecl(d);
		capture = new CaptureNextTokenType(factory,expr.varName);
	}
}
