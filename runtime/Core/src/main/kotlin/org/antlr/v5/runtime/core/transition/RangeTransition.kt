package org.antlr.v5.runtime.core.transition

import org.antlr.v5.runtime.core.misc.IntervalSet
import org.antlr.v5.runtime.core.state.ATNState

public class RangeTransition(target: ATNState, public val from: Int, public val to: Int) : Transition(target) {
  override val serializationType: Int =
    RANGE

  override fun label(): IntervalSet =
    IntervalSet.of(from, to)

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    symbol in from..to

  override fun toString(): String {
    val buf = StringBuilder("'")
    buf.appendCodePoint(from)
    buf.append("'..'")
    buf.appendCodePoint(to)
    buf.append("'")
    return buf.toString()
  }
}