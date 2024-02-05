/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin.tree.xpath

import org.antlr.v5.runtime.kotlin.tree.ParseTree
import org.antlr.v5.runtime.kotlin.tree.Trees

/**
 * Either `ID` at start of path or `...//ID` in middle of path.
 */
public open class XPathRuleAnywhereElement(ruleName: String, protected var ruleIndex: Int) : XPathElement(ruleName) {
  override fun evaluate(t: ParseTree): Collection<ParseTree> =
    Trees.findAllRuleNodes(t, ruleIndex)
}
