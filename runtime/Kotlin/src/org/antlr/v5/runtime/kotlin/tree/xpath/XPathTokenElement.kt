/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin.tree.xpath

import org.antlr.v5.runtime.kotlin.tree.ParseTree
import org.antlr.v5.runtime.kotlin.tree.TerminalNode
import org.antlr.v5.runtime.kotlin.tree.Trees

@Suppress("MemberVisibilityCanBePrivate")
public open class XPathTokenElement(tokenName: String, protected var tokenType: Int) : XPathElement(tokenName) {
  override fun evaluate(t: ParseTree): Collection<ParseTree> {
    // Return all children of t that match nodeName
    val children = Trees.getChildren(t)
    val nodes = ArrayList<ParseTree>(children.size)

    for (c in children) {
      if (c is TerminalNode) {
        if (c.symbol.type == tokenType && !invert || c.symbol.type != tokenType && invert) {
          nodes.add(c)
        }
      }
    }

    return nodes
  }
}
