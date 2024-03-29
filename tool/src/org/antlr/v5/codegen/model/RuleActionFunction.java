/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.tool.Rule;

import java.util.LinkedHashMap;

public class RuleActionFunction extends OutputModelObject {
	public final String name;
	public final String escapedName;
	public final String ctxType;
	public final int ruleIndex;

	/** Map actionIndex to Action */
	@ModelElement public LinkedHashMap<Integer, Action> actions =
		new LinkedHashMap<Integer, Action>();

	public RuleActionFunction(OutputModelFactory factory, Rule r, String ctxType) {
		super(factory);
		name = r.name;
		escapedName = factory.getGenerator().getTarget().escapeIfNeeded(name);
		ruleIndex = r.index;
		this.ctxType = ctxType;
	}
}
