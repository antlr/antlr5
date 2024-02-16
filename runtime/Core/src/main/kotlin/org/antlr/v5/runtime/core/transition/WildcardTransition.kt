package org.antlr.v5.runtime.core.transition

import org.antlr.v5.runtime.core.state.ATNState

public class WildcardTransition(target: ATNState) : Transition(target) {
  override val serializationType: Int =
    WILDCARD

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    symbol in minVocabSymbol..maxVocabSymbol

  override fun toString(): String =
    "."
}