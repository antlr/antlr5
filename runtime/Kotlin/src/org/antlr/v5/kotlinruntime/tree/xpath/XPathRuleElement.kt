/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.kotlinruntime.tree.xpath

import org.antlr.v5.kotlinruntime.ParserRuleContext
import org.antlr.v5.kotlinruntime.tree.ParseTree
import org.antlr.v5.kotlinruntime.tree.Trees

public open class XPathRuleElement(ruleName: String, protected var ruleIndex: Int) : XPathElement(ruleName) {
  override fun evaluate(t: ParseTree): Collection<ParseTree> {
    // Return all children of t that match nodeName
    val children = Trees.getChildren(t)
    val nodes = ArrayList<ParseTree>(children.size)

    for (c in children) {
      if (c is ParserRuleContext) {
        if (c.ruleIndex == ruleIndex && !invert || c.ruleIndex != ruleIndex && invert) {
          nodes.add(c)
        }
      }
    }

    return nodes
  }
}
