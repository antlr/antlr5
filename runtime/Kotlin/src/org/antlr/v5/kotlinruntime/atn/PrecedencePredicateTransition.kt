/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.kotlinruntime.atn

/**
 * @author Sam Harwell
 */
public class PrecedencePredicateTransition(
  target: ATNState,
  public val precedence: Int,
) : AbstractPredicateTransition(target) {

  override val serializationType: Int =
    PRECEDENCE

  override val isEpsilon: Boolean =
    true

  public val predicate: SemanticContext.PrecedencePredicate
    get() = SemanticContext.PrecedencePredicate(precedence)

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    false

  override fun toString(): String =
    "$precedence >= _p"
}
