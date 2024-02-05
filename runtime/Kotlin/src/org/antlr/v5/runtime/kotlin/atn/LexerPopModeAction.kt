/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.atn

import org.antlr.v5.runtime.kotlin.Lexer
import org.antlr.v5.runtime.kotlin.atn.LexerPopModeAction.Companion.INSTANCE
import org.antlr.v5.runtime.kotlin.misc.MurmurHash

/**
 * Implements the `popMode` lexer action by calling [Lexer.popMode].
 *
 * The `popMode` command does not have any parameters, so this action is
 * implemented as a singleton instance exposed by [INSTANCE].
 *
 * @author Sam Harwell
 * @since 4.2
 */
public class LexerPopModeAction private constructor() : LexerAction {
  public companion object {
    /**
     * Provides a singleton instance of this parameterless lexer action.
     */
    public val INSTANCE: LexerPopModeAction = LexerPopModeAction()
  }

  /**
   * Returns [LexerActionType.POP_MODE].
   */
  override val actionType: LexerActionType =
    LexerActionType.POP_MODE

  /**
   * Returns `false`.
   */
  override val isPositionDependent: Boolean =
    false

  /**
   * This action is implemented by calling [Lexer.popMode].
   */
  override fun execute(lexer: Lexer) {
    lexer.popMode()
  }

  override fun hashCode(): Int {
    var hash = MurmurHash.initialize()
    hash = MurmurHash.update(hash, actionType.ordinal)
    return MurmurHash.finish(hash, 1)
  }

  override fun equals(other: Any?): Boolean =
    other === this

  override fun toString(): String =
    "popMode"
}
