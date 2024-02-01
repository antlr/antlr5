/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.kotlinruntime.atn

import org.antlr.v5.kotlinruntime.Lexer
import org.antlr.v5.kotlinruntime.misc.MurmurHash

/**
 * Implements the `channel` lexer action by setting [Lexer.channel]
 * with the assigned channel.
 *
 * @param channel The channel value to assign to [Lexer.channel]
 *
 * @author Sam Harwell
 * @since 4.2
 */
public class LexerChannelAction(public val channel: Int) : LexerAction {
  /**
   * Set to [LexerActionType.CHANNEL] by default.
   */
  override val actionType: LexerActionType =
    LexerActionType.CHANNEL

  /**
   * Set to `false` by default.
   */
  override val isPositionDependent: Boolean =
    false

  /**
   * This action is implemented by assigning [Lexer.channel] with the
   * value provided by [channel].
   */
  override fun execute(lexer: Lexer) {
    lexer.channel = channel
  }

  override fun hashCode(): Int {
    var hash = MurmurHash.initialize()
    hash = MurmurHash.update(hash, actionType.ordinal)
    hash = MurmurHash.update(hash, channel)
    return MurmurHash.finish(hash, 2)
  }

  override fun equals(other: Any?): Boolean {
    if (other === this) {
      return true
    }

    if (other !is LexerChannelAction) {
      return false
    }

    return channel == other.channel
  }

  override fun toString(): String =
    "channel($channel)"
}
