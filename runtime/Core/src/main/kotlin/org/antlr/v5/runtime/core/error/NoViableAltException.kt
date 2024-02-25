/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.core.error

import org.antlr.v5.runtime.core.Parser
import org.antlr.v5.runtime.core.Token
import org.antlr.v5.runtime.core.TokenStream
import org.antlr.v5.runtime.core.atn.ATNConfigSet
import org.antlr.v5.runtime.core.context.ParserRuleContext

/**
 * Indicates that the parser could not decide which of two or more paths
 * to take based upon the remaining input.
 *
 * It tracks the starting token of the offending input and also knows where
 * the parser was in the various paths when the error.
 *
 * Reported by `reportNoViableAlternative()`.
 *
 * @param startToken The token object at the start index; the input stream might
 *   not be buffering tokens so get a reference to it. (At the
 *   time the error occurred, of course the stream needs to keep a
 *   buffer all the tokens, but later we might not have access to those)
 * @param deadEndConfigs Which configurations did we try at `input.index()`
 *   that couldn't match `input.LT(1)`?
 */
public class NoViableAltException(
    recognizer: Parser,
    input: TokenStream = recognizer.tokenStream,
    public val startToken: Token? = recognizer.currentToken,
    offendingToken: Token? = recognizer.currentToken,
    public val deadEndConfigs: ATNConfigSet? = null,
    ctx: ParserRuleContext? = recognizer.context,
) : RecognitionException(recognizer, input, ctx) {
  init {
    this.offendingToken = offendingToken
  }

    constructor(recognizer: Parser) : this(recognizer, recognizer.tokenStream, recognizer.currentToken, recognizer.currentToken, null, recognizer.context)
}
