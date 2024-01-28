// Copyright 2017-present Strumenta and contributors, licensed under Apache 2.0.
// Copyright 2024-present Strumenta and contributors, licensed under BSD 3-Clause.
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
