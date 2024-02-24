/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime._unused.tree.xpath;

import org.antlr.v5.runtime.core.tree.ParseTree;
import org.antlr.v5.runtime.core.tree.TerminalNode;
import org.antlr.v5.runtime.core.tree.Tree;
import org.antlr.v5.runtime.core.tree.Trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class XPathTokenElement extends XPathElement {
	protected int tokenType;
	public XPathTokenElement(String tokenName, int tokenType) {
		super(tokenName);
		this.tokenType = tokenType;
	}

	@Override
	public Collection<ParseTree> evaluate(ParseTree t) {
		// return all children of t that match nodeName
		List<ParseTree> nodes = new ArrayList<ParseTree>();
		for (Tree c : Trees.INSTANCE.getChildren(t)) {
			if ( c instanceof TerminalNode ) {
				TerminalNode tnode = (TerminalNode)c;
				if ( (tnode.getSymbol().getType() == tokenType && !invert) ||
					 (tnode.getSymbol().getType() != tokenType && invert) )
				{
					nodes.add(tnode);
				}
			}
		}
		return nodes;
	}
}
