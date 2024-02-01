/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.kotlinruntime.atn

import org.antlr.v5.kotlinruntime.Lexer
import org.antlr.v5.kotlinruntime.misc.MurmurHash

/**
 * Implements the `mode` lexer action by calling [Lexer.mode] with
 * the assigned mode.
 *
 * @param mode The mode value to pass to [Lexer.mode]
 *
 * @author Sam Harwell
 * @since 4.2
 */
public class LexerModeAction(public val mode: Int) : LexerAction {
  /**
   * Returns [LexerActionType.MODE].
   */
  override val actionType: LexerActionType =
    LexerActionType.MODE

  /**
   * Returns `false`.
   */
  override val isPositionDependent: Boolean =
    false

  /**
   * This action is implemented by calling [Lexer.mode] with [mode].
   */
  override fun execute(lexer: Lexer): Unit =
    lexer.mode(mode)

  override fun hashCode(): Int {
    var hash = MurmurHash.initialize()
    hash = MurmurHash.update(hash, actionType.ordinal)
    hash = MurmurHash.update(hash, mode)
    return MurmurHash.finish(hash, 2)
  }

  override fun equals(other: Any?): Boolean {
    if (other === this) {
      return true
    }

    if (other !is LexerModeAction) {
      return false
    }

    return mode == other.mode
  }

  override fun toString(): String =
    "mode($mode)"
}
