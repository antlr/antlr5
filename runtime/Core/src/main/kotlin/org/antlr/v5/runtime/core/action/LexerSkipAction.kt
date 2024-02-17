/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.core.action

import org.antlr.v5.runtime.core.Lexer
import org.antlr.v5.runtime.core.misc.MurmurHash


/**
 * Implements the `skip` lexer action by calling [Lexer.skip].
 *
 * The `skip` command does not have any parameters, so this action is
 * implemented as a singleton instance exposed by [INSTANCE].
 *
 * @author Sam Harwell
 * @since 4.2
 */
public class LexerSkipAction private constructor() : LexerAction {
  public companion object {
    /**
     * Provides a singleton instance of this parameterless lexer action.
     */
    public val INSTANCE: LexerSkipAction = LexerSkipAction()
  }

  /**
   * Returns [LexerActionType.SKIP].
   */
  override val actionType: LexerActionType =
    LexerActionType.SKIP

  /**
   * Returns `false`.
   */
  override val isPositionDependent: Boolean =
    false

  /**
   * This action is implemented by calling [Lexer.skip].
   */
  override fun execute(lexer: Lexer): Unit =
    lexer.skip()

  override fun hashCode(): Int {
    var hash = MurmurHash.initialize()
    hash = MurmurHash.update(hash, actionType.ordinal)
    return MurmurHash.finish(hash, 1)
  }

  override fun equals(other: Any?): Boolean =
    other === this

  override fun toString(): String =
    "skip"
}
