/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.core.tree

import org.antlr.v5.runtime.core.Parser
import org.antlr.v5.runtime.core.Token
import org.antlr.v5.runtime.core.context.RuleContext
import org.antlr.v5.runtime.core.misc.Interval

public open class TerminalNodeImpl(override var symbol: Token) : TerminalNode {
  private var parent: ParseTree? = null

  override val childCount: Int = 0

  override val text: String
    get() = symbol.text!!

  override val payload: Token
    get() = symbol

  override val sourceInterval: Interval
    get() {
      val tokenIndex = symbol.tokenIndex
      return Interval(tokenIndex, tokenIndex)
    }

  override fun getParent(): ParseTree? =
    parent

  override fun setParent(value: RuleContext?) {
    parent = value
  }

  override fun getChild(i: Int): ParseTree? =
    null

  override fun <T> accept(visitor: ParseTreeVisitor<out T>): T? =
    visitor.visitTerminal(this)

  override fun toStringTree(parser: Parser): String =
    toString()

  override fun toString(): String =
    if (symbol.type == Token.EOF) {
      "<EOF>"
    } else {
      symbol.text!!
    }

  override fun toStringTree(): String =
    toString()
}
