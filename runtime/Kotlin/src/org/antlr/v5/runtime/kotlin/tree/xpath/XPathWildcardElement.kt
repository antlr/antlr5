/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin.tree.xpath

import org.antlr.v5.runtime.kotlin.tree.ParseTree
import org.antlr.v5.runtime.kotlin.tree.Trees

public open class XPathWildcardElement : XPathElement(XPath.WILDCARD) {
  override fun evaluate(t: ParseTree): Collection<ParseTree> {
    if (invert) {
      // !* is weird but valid (empty)
      return emptyList()
    }

    val children = Trees.getChildren(t)
    val kids = ArrayList<ParseTree>(children.size)

    for (c in children) {
      kids.add(c as ParseTree)
    }

    return kids
  }
}