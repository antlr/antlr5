/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.core.error

import org.antlr.v5.runtime.core.IntStream
import org.antlr.v5.runtime.core.Recognizer
import org.antlr.v5.runtime.core.Token
import org.antlr.v5.runtime.core.state.DecisionState
import org.antlr.v5.runtime.core.context.ParserRuleContext
import org.antlr.v5.runtime.core.misc.IntervalSet

/**
 * The root of the ANTLR exception hierarchy.
 *
 * In general, ANTLR tracks just 3 kinds of errors: prediction errors,
 * failed predicate errors, and mismatched input errors.
 *
 * In each case, the parser knows where it is in the input,
 * where it is in the ATN, the rule invocation stack,
 * and what kind of problem occurred.
 *
 * @param recognizer The [Recognizer] where this exception originated,
 *   or `null` if not available
 * @param inputStream The input stream which is the symbol source for
 *   the recognizer where this exception was thrown
 * @param ctx The [RuleContext] at the time this exception was thrown,
 *   or `null` if not available
 */
public open class RecognitionException(
    public val recognizer: Recognizer<*, *>?,
    public open val inputStream: IntStream,
    public val ctx: ParserRuleContext?,
    message: String? = null,
) : RuntimeException(message) {
  /**
   * The current [Token] when an error occurred.
   *
   * Since not all streams support accessing symbols by index,
   * we have to track the [Token] instance itself.
   */
  public var offendingToken: Token? = null
    protected set

  /**
   * The ATN state number the parser was in at the time the error occurred.
   *
   * For [NoViableAltException] and [LexerNoViableAltException] exceptions,
   * this is the [DecisionState] number. For others, it is the state whose
   * outgoing edge we couldn't match.
   *
   * If the state number is not known, this method returns `-1`.
   */
  public var offendingState: Int = -1
    protected set

  /**
   * The set of input symbols which could potentially follow the
   * previously matched symbol at the time this exception was thrown.
   *
   * If the set of expected tokens is not known and could not be computed,
   * this is `null`.
   */
  public val expectedTokens: IntervalSet?
    get() = recognizer?.atn?.getExpectedTokens(offendingState, ctx)

  init {
    if (recognizer != null) {
      offendingState = recognizer.state
    }
  }
}
