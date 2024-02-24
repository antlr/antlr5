/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime._unused.tree.xpath;

import org.antlr.v5.runtime.core.context.ParserRuleContext;
import org.antlr.v5.runtime.core.tree.ParseTree;
import org.antlr.v5.runtime.core.tree.Tree;
import org.antlr.v5.runtime.core.tree.Trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XPathRuleElement extends XPathElement {
	protected int ruleIndex;
	public XPathRuleElement(String ruleName, int ruleIndex) {
		super(ruleName);
		this.ruleIndex = ruleIndex;
	}

	@Override
	public Collection<ParseTree> evaluate(ParseTree t) {
				// return all children of t that match nodeName
		List<ParseTree> nodes = new ArrayList<ParseTree>();
		for (Tree c : Trees.INSTANCE.getChildren(t)) {
			if ( c instanceof ParserRuleContext ) {
				ParserRuleContext ctx = (ParserRuleContext)c;
				if ( (ctx.getRuleIndex() == ruleIndex && !invert) ||
					 (ctx.getRuleIndex() != ruleIndex && invert) )
				{
					nodes.add(ctx);
				}
			}
		}
		return nodes;
	}
}
