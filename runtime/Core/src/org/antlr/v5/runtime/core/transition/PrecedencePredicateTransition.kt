package org.antlr.v5.runtime.core.transition

import org.antlr.v5.runtime.core.context.SemanticContext
import org.antlr.v5.runtime.core.state.ATNState

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