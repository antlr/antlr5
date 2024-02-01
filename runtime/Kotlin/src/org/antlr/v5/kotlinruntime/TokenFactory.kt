/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.kotlinruntime

/**
 * The default mechanism for creating tokens. It's used by default in [Lexer] and
 * the error handling strategy (to create missing tokens). Notifying the [Parser]
 * of a new factory means that it notifies its token source and error strategy.
 */
public interface TokenFactory<Symbol : Token> {
  /**
   * This is the method used to create tokens in the lexer and in the
   * error handling strategy. If `text != null`, than the start and stop positions
   * are wiped to `-1` in the text override is set in the [CommonToken].
   */
  public fun create(
    source: Pair<TokenSource?, CharStream?>,
    type: Int,
    text: String?,
    channel: Int,
    start: Int,
    stop: Int,
    line: Int,
    charPositionInLine: Int,
  ): Symbol

  /**
   * Generically useful.
   */
  public fun create(type: Int, text: String): Symbol
}
