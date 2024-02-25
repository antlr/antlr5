/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.core.error

import org.antlr.v5.runtime.core.Parser
import org.antlr.v5.runtime.core.transition.AbstractPredicateTransition
import org.antlr.v5.runtime.core.transition.PredicateTransition

/**
 * A semantic predicate failed during validation.
 *
 * Validation of predicates occurs when normally parsing the alternative
 * just like matching a token. Disambiguating predicate evaluation occurs
 * when we test a predicate during prediction.
 */
@Suppress("MemberVisibilityCanBePrivate")
public class FailedPredicateException(
    recognizer: Parser,
    public val predicate: String? = null,
    message: String? = null,
) : RecognitionException(
  recognizer,
  recognizer.tokenStream,
  recognizer.context!!,
  formatMessage(predicate, message),
) {
  private companion object {
    private fun formatMessage(predicate: String?, message: String?): String =
      message ?: "failed predicate: {$predicate}?"
  }

  public var ruleIndex: Int = -1
  public var predIndex: Int = -1

  init {
    val s = recognizer.interpreter!!.atn.states[recognizer.state]
    val trans = s!!.transition(0) as AbstractPredicateTransition

    if (trans is PredicateTransition) {
      ruleIndex = trans.ruleIndex
      predIndex = trans.predIndex
    } else {
      ruleIndex = 0
      predIndex = 0
    }

    offendingToken = recognizer.currentToken
  }
}
