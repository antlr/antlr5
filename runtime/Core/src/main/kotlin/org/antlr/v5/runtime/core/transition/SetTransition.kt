package org.antlr.v5.runtime.core.transition

import org.antlr.v5.runtime.core.Token
import org.antlr.v5.runtime.core.misc.IntervalSet
import org.antlr.v5.runtime.core.state.ATNState

/**
 * A transition containing a set of values.
 *
 * TODO(sam): should we really allow null here?
 */
public open class SetTransition(target: ATNState, set: IntervalSet?) : Transition(target) {
  public val set: IntervalSet = set ?: IntervalSet.of(Token.INVALID_TYPE)

  override val serializationType: Int =
    SET

  override fun label(): IntervalSet =
    set

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    set.contains(symbol)

  override fun toString(): String =
    set.toString()
}