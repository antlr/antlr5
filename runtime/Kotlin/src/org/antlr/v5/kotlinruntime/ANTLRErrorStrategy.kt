/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.kotlinruntime

/**
 * The interface for defining strategies to deal with syntax errors encountered
 * during a parse by ANTLR-generated parsers. We distinguish between three
 * different kinds of errors:
 *
 *  - The parser could not figure out which path to take in the ATN
 *    (none of the available alternatives could possibly match)
 *  - The current input does not match what we were looking for
 *  - A predicate evaluated to false
 *
 * Implementations of this interface report syntax errors by calling [Parser.notifyErrorListeners].
 *
 * TODO: what to do about lexers
 */
public interface ANTLRErrorStrategy {
  /**
   * Reset the error handler state for the specified [recognizer].
   *
   * @param recognizer The parser instance
   */
  public fun reset(recognizer: org.antlr.v5.kotlinruntime.Parser)

  /**
   * This method is called when an unexpected symbol is encountered during an
   * inline match operation, such as [Parser.match]. If the error
   * strategy successfully recovers from the match failure, this method
   * returns the [Token] instance which should be treated as the
   * successful result of the match.
   *
   * This method handles the consumption of any tokens - the caller should
   * **not** call [Parser.consume] after a successful recovery.
   *
   * Note that the calling code will not report an error if this method
   * returns successfully. The error strategy implementation is responsible
   * for calling [Parser.notifyErrorListeners] as appropriate.
   *
   * @param recognizer The parser instance
   * @throws RecognitionException If the error strategy was not able to
   *   recover from the unexpected input symbol
   */
  public fun recoverInline(recognizer: org.antlr.v5.kotlinruntime.Parser): org.antlr.v5.kotlinruntime.Token

  /**
   * This method is called to recover from exception [e].
   *
   * This method is called after [reportError] by the default
   * exception handler generated for a rule method.
   *
   * @param recognizer The parser instance
   * @param e The recognition exception to recover from
   * @throws RecognitionException If the error strategy could not recover from
   *   the recognition exception
   * @see reportError
   */
  public fun recover(recognizer: org.antlr.v5.kotlinruntime.Parser, e: org.antlr.v5.kotlinruntime.RecognitionException)

  /**
   * This method provides the error handler with an opportunity to handle
   * syntactic or semantic errors in the input stream before they result in a
   * [RecognitionException].
   *
   * The generated code currently contains calls to [sync] after
   * entering the decision state of a closure block (`(...)*` or `(...)+`).
   *
   * For an implementation based on Jim Idle's "magic sync" mechanism,
   * see [DefaultErrorStrategy.sync].
   *
   * @param recognizer The parser instance
   * @throws RecognitionException If an error is detected by the error
   *   strategy but cannot be automatically recovered at the current state in
   *   the parsing process
   * @see DefaultErrorStrategy.sync
   */
  public fun sync(recognizer: org.antlr.v5.kotlinruntime.Parser)

  /**
   * Tests whether `recognizer` is in the process of recovering from an error.
   *
   * In error recovery mode, [Parser.consume] adds symbols to the parse tree
   * by calling [Parser.createErrorNode] then [ParserRuleContext.addErrorNode]
   * instead of [Parser.createTerminalNode].
   *
   * @param recognizer The parser instance
   * @return `true` if the parser is currently recovering from a parse error, otherwise `false`
   */
  public fun inErrorRecoveryMode(recognizer: org.antlr.v5.kotlinruntime.Parser): Boolean

  /**
   * This method is called by when the parser successfully matches an input symbol.
   *
   * @param recognizer The parser instance
   */
  public fun reportMatch(recognizer: org.antlr.v5.kotlinruntime.Parser)

  /**
   * Report any kind of [RecognitionException].
   *
   * This method is called by the default exception handler generated for a rule method.
   *
   * @param recognizer The parser instance
   * @param e The recognition exception to report
   */
  public fun reportError(recognizer: org.antlr.v5.kotlinruntime.Parser, e: org.antlr.v5.kotlinruntime.RecognitionException)
}
