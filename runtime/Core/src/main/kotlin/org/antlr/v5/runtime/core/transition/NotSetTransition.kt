package org.antlr.v5.runtime.core.transition

import org.antlr.v5.runtime.core.misc.IntervalSet
import org.antlr.v5.runtime.core.state.ATNState

public class NotSetTransition(target: ATNState, set: IntervalSet) : SetTransition(target, set) {
  override val serializationType: Int =
    NOT_SET

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    symbol in minVocabSymbol..maxVocabSymbol && !super.matches(symbol, minVocabSymbol, maxVocabSymbol)

  override fun toString(): String =
    "~${super.toString()}"
}