/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model.decl;

import org.antlr.v5.codegen.OutputModelFactory;

/** {@code public Token X() { }} */
public class ContextTokenGetterDecl extends ContextGetterDecl {
	public boolean optional;

	public ContextTokenGetterDecl(OutputModelFactory factory, String name, boolean optional) {
		this(factory, name, optional, false);
	}

	public ContextTokenGetterDecl(OutputModelFactory factory, String name, boolean optional, boolean signature) {
		super(factory, name, signature);
		this.optional = optional;
	}

	@Override
	public ContextGetterDecl getSignatureDecl() {
		return new ContextTokenGetterDecl(factory, name, optional, true);
	}
}
