/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.tool.Rule;

public class RuleSempredFunction extends RuleActionFunction {
	public RuleSempredFunction(OutputModelFactory factory, Rule r, String ctxType) {
		super(factory, r, ctxType);
	}
}
