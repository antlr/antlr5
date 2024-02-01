/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.kotlinruntime.atn

import org.antlr.v5.kotlinruntime.CharStream
import org.antlr.v5.kotlinruntime.Lexer

/**
 * Represents a single action which can be executed following the successful
 * match of a lexer rule.
 *
 * Lexer actions are used for both embedded action syntax
 * and ANTLR 4's new lexer command syntax.
 *
 * @author Sam Harwell
 * @since 4.2
 */
public interface LexerAction {
  /**
   * The serialization type of the lexer action.
   */
  public val actionType: LexerActionType

  /**
   * Whether the lexer action is position-dependent.
   *
   * Position-dependent actions may have different semantics depending
   * on the [CharStream] index at the time the action is executed.
   *
   * Many lexer commands, including `type`, `skip`, and `more`,
   * do not check the input index during their execution.
   *
   * Actions like this are position-independent, and may be stored more
   * efficiently as part of the [LexerATNConfig.lexerActionExecutor].
   *
   * @return `true` if the lexer action semantics can be affected by the
   *   position of the input [CharStream] at the time it is executed,
   *   otherwise `false`
   */
  public val isPositionDependent: Boolean

  /**
   * Execute the lexer action in the context of the specified [Lexer].
   *
   * For position-dependent actions, the input stream must already be
   * positioned correctly prior to calling this method.
   *
   * @param lexer The lexer instance
   */
  public fun execute(lexer: Lexer)
}
