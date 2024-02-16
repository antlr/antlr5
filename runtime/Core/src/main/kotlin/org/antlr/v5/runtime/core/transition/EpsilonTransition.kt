package org.antlr.v5.runtime.core.transition

import org.antlr.v5.runtime.core.state.ATNState

public class EpsilonTransition(target: ATNState, outermostPrecedenceReturn: Int = -1) : Transition(target) {
  override val serializationType: Int =
    EPSILON

  override val isEpsilon: Boolean =
    true

  /**
   * The rule index of a precedence rule for which this transition is
   * returning from, where the precedence value is `0`, otherwise `-1`.
   *
   * @see ATNConfig.isPrecedenceFilterSuppressed
   * @see ParserATNSimulator.applyPrecedenceFilter
   *
   * @since 4.4.1
   */
  @Suppress("CanBePrimaryConstructorProperty")
  public val outermostPrecedenceReturn: Int =
    outermostPrecedenceReturn

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    false

  override fun toString(): String =
    "epsilon"
}