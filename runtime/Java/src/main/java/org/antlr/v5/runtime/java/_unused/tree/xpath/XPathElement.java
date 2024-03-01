/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.java._unused.tree.xpath;

import org.antlr.v5.runtime.core.tree.ParseTree;

import java.util.Collection;

public abstract class XPathElement {
	protected String nodeName;
	protected boolean invert;

	/** Construct element like {@code /ID} or {@code ID} or {@code /*} etc...
	 *  op is null if just node
	 */
	public XPathElement(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * Given tree rooted at {@code t} return all nodes matched by this path
	 * element.
	 */
	public abstract Collection<ParseTree> evaluate(ParseTree t);

	@Override
	public String toString() {
		String inv = invert ? "!" : "";
		return getClass().getSimpleName()+"["+inv+nodeName+"]";
	}
}
