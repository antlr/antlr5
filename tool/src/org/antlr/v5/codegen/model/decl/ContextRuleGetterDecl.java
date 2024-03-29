/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model.decl;

import org.antlr.v5.codegen.OutputModelFactory;

/** {@code public XContext X() { }} */
public class ContextRuleGetterDecl extends ContextGetterDecl {
	public String ctxName;
	public boolean optional;

	public ContextRuleGetterDecl(OutputModelFactory factory, String name, String ctxName, boolean optional) {
		this(factory, name, ctxName, optional, false);
	}

	public ContextRuleGetterDecl(OutputModelFactory factory, String name, String ctxName, boolean optional, boolean signature) {
		super(factory, name, signature);
		this.ctxName = ctxName;
		this.optional = optional;
	}

	@Override
	public ContextGetterDecl getSignatureDecl() {
		return new ContextRuleGetterDecl(factory, name, ctxName, optional, true);
	}
}
