/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.tool.ast.GrammarAST;

/** */
public abstract class OutputModelObject {
	public OutputModelFactory factory;
	public GrammarAST ast;

	public OutputModelObject() {}

	public OutputModelObject(OutputModelFactory factory) { this(factory, null); }

	public OutputModelObject(OutputModelFactory factory, GrammarAST ast) {
		this.factory = factory;
		this.ast = ast;
	}
}
