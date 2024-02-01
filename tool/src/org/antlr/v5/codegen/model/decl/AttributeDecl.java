/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model.decl;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.tool.Attribute;

/** */
public class AttributeDecl extends Decl {
	public String type;
	public String initValue;
	public AttributeDecl(OutputModelFactory factory, Attribute a) {
		super(factory, a.name, a.decl);
		this.type = a.type;
		this.initValue = a.initValue;
	}
}
