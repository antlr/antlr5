/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.kotlinruntime.tree.xpath

import org.antlr.v5.kotlinruntime.tree.ParseTree
import org.antlr.v5.kotlinruntime.tree.Trees

public open class XPathWildcardAnywhereElement : XPathElement(XPath.WILDCARD) {
  override fun evaluate(t: ParseTree): Collection<ParseTree> =
    if (invert) {
      // !* is weird but valid (empty)
      emptyList()
    } else {
      Trees.getDescendants(t)
    }
}
