/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model.decl;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.codegen.model.DispatchMethod;
import org.antlr.v5.codegen.model.ListenerDispatchMethod;
import org.antlr.v5.codegen.model.VisitorDispatchMethod;
import org.antlr.v5.tool.Rule;

import java.util.ArrayList;

/** A StructDecl to handle a -&gt; label on alt */
public class AltLabelStructDecl extends StructDecl {
	public int altNum;
	public String parentRule;
	public AltLabelStructDecl(OutputModelFactory factory, Rule r,
							  int altNum, String label)
	{
		// override name set in super to the label ctx
		super(factory, r, factory.getGenerator().getTarget().getAltLabelContextStructName(label));
		this.altNum = altNum;
		this.parentRule = r.name;
		derivedFromName = label;
	}

	@Override
	public void addDispatchMethods(Rule r) {
		dispatchMethods = new ArrayList<DispatchMethod>();
		if ( factory.getGrammar().tool.gen_listener ) {
			dispatchMethods.add(new ListenerDispatchMethod(factory, true));
			dispatchMethods.add(new ListenerDispatchMethod(factory, false));
		}
		if ( factory.getGrammar().tool.gen_visitor ) {
			dispatchMethods.add(new VisitorDispatchMethod(factory));
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == this ) return true;
		if (!(obj instanceof AltLabelStructDecl)) return false;

		return name.equals(((AltLabelStructDecl)obj).name);
	}
}
