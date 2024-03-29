/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model.decl;

import org.antlr.v5.codegen.OutputModelFactory;

public class ContextRuleListIndexedGetterDecl extends ContextRuleListGetterDecl {
	public ContextRuleListIndexedGetterDecl(OutputModelFactory factory, String name, String ctxName) {
		this(factory, name, ctxName, false);
	}

	public ContextRuleListIndexedGetterDecl(OutputModelFactory factory, String name, String ctxName, boolean signature) {
		super(factory, name, ctxName, signature);
	}

	@Override
	public String getArgType() {
		return "int";
	}

	@Override
	public ContextGetterDecl getSignatureDecl() {
		return new ContextRuleListIndexedGetterDecl(factory, name, ctxName, true);
	}
}
