/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model.decl;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.codegen.model.SrcOp;

/** */
public class Decl extends SrcOp {
	public final String name;
	public final String escapedName;
	public final String decl; 	// whole thing if copied from action
	public boolean isLocal; // if local var (not in RuleContext struct)
	public StructDecl ctx;  // which context contains us? set by addDecl

	public Decl(OutputModelFactory factory, String name) {
		this(factory, name, null);
	}

	public Decl(OutputModelFactory factory, String name, String decl) {
		super(factory);
		this.name = name;
		this.escapedName = factory.getGenerator().getTarget().escapeIfNeeded(name);
		this.decl = decl;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	/** If same name, can't redefine, unless it's a getter */
	@Override
	public boolean equals(Object obj) {
		if ( this==obj ) return true;
		if ( !(obj instanceof Decl) ) return false;
		// A() and label A are different
		if ( obj instanceof ContextGetterDecl ) return false;
		return name.equals(((Decl) obj).name);
	}
}
