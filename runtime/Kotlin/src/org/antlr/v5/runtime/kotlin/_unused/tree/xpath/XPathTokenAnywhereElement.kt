/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin._unused.tree.xpath

import org.antlr.v5.runtime.core.tree.ParseTree
import org.antlr.v5.runtime.core.tree.Trees

@Suppress("MemberVisibilityCanBePrivate")
public open class XPathTokenAnywhereElement(tokenName: String, protected var tokenType: Int) : XPathElement(tokenName) {
  override fun evaluate(t: ParseTree): Collection<ParseTree> =
    Trees.findAllTokenNodes(t, tokenType)
}
