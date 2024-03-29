/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.java._unused.tree.xpath;

import org.antlr.v5.runtime.core.tree.ParseTree;
import org.antlr.v5.runtime.core.tree.Trees;

import java.util.Collection;

/**
 * Either {@code ID} at start of path or {@code ...//ID} in middle of path.
 */
public class XPathRuleAnywhereElement extends XPathElement {
	protected int ruleIndex;
	public XPathRuleAnywhereElement(String ruleName, int ruleIndex) {
		super(ruleName);
		this.ruleIndex = ruleIndex;
	}

	@Override
	public Collection<ParseTree> evaluate(ParseTree t) {
		return Trees.INSTANCE.findAllRuleNodes(t, ruleIndex);
	}
}
