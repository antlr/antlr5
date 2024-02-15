/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.tree

import org.antlr.v5.runtime.core.context.ParserRuleContext
import org.antlr.v5.runtime.core.tree.*
import kotlin.jvm.JvmStatic

public open class ParseTreeWalker {
  public companion object {
    @JvmStatic
    public val DEFAULT: ParseTreeWalker = ParseTreeWalker()
  }

  /**
   * Performs a walk on the given parse tree starting at the root and going down recursively
   * with depth-first search. On each node, [ParseTreeWalker.enterRule] is called before
   * recursively walking down into child nodes, then [ParseTreeWalker.exitRule] is called
   * after the recursive call to wind up.
   *
   * @param listener The listener used by the walker to process grammar rules
   * @param t The parse tree to be walked on
   */
  public open fun walk(listener: ParseTreeListener, t: ParseTree) {
    if (t is ErrorNode) {
      listener.visitErrorNode(t)
      return
    } else if (t is TerminalNode) {
      listener.visitTerminal(t)
      return
    }

    val r = t as RuleNode
    enterRule(listener, r)

    val n = r.childCount

    for (i in 0..<n) {
      walk(listener, r.getChild(i)!!)
    }

    exitRule(listener, r)
  }

  /**
   * Enters a grammar rule by first triggering the generic event [ParseTreeListener.enterEveryRule]
   * then by triggering the event specific to the given parse tree node.
   *
   * @param listener The listener responding to the trigger events
   * @param r The grammar rule containing the rule context
   */
  protected fun enterRule(listener: ParseTreeListener, r: RuleNode) {
    val ctx = r.ruleContext as ParserRuleContext
    listener.enterEveryRule(ctx)
    ctx.enterRule(listener)
  }

  /**
   * Exits a grammar rule by first triggering the event specific to the given parse tree node
   * then by triggering the generic event [ParseTreeListener.exitEveryRule].
   *
   * @param listener The listener responding to the trigger events
   * @param r The grammar rule containing the rule context
   */
  protected fun exitRule(listener: ParseTreeListener, r: RuleNode) {
    val ctx = r.ruleContext as ParserRuleContext
    ctx.exitRule(listener)
    listener.exitEveryRule(ctx)
  }
}
