/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.kotlinruntime

import org.antlr.v5.kotlinruntime.atn.ATNConfigSet
import org.antlr.v5.kotlinruntime.misc.Interval
import org.antlr.v5.kotlinruntime.misc.Utils

/**
 * @param startIndex Matching attempted at what input index?
 * @param deadEndConfigs Which configurations did we try at `input.index()`
 *   that couldn't match `input.LA(1)`?
 */
@Suppress("MemberVisibilityCanBePrivate")
public class LexerNoViableAltException(
  lexer: Lexer,
  input: CharStream,
  public val startIndex: Int,
  public val deadEndConfigs: ATNConfigSet?,
) : RecognitionException(lexer, input, null) {
  override val inputStream: CharStream
    get() = super.inputStream as CharStream

  override fun toString(): String {
    var symbol = ""

    if (startIndex >= 0 && startIndex < inputStream.size()) {
      symbol = inputStream.getText(Interval.of(startIndex, startIndex))
      symbol = Utils.escapeWhitespace(symbol, false)
    }

    return "${this::class.simpleName}('$symbol')"
  }
}
