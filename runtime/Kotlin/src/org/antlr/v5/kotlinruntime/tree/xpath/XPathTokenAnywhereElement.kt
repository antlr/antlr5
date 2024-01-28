// Copyright 2017-present Strumenta and contributors, licensed under Apache 2.0.
// Copyright 2024-present Strumenta and contributors, licensed under BSD 3-Clause.
package org.antlr.v5.kotlinruntime.tree.xpath

import org.antlr.v5.kotlinruntime.tree.ParseTree
import org.antlr.v5.kotlinruntime.tree.Trees

@Suppress("MemberVisibilityCanBePrivate")
public open class XPathTokenAnywhereElement(tokenName: String, protected var tokenType: Int) : XPathElement(tokenName) {
  override fun evaluate(t: ParseTree): Collection<ParseTree> =
    Trees.findAllTokenNodes(t, tokenType)
}
