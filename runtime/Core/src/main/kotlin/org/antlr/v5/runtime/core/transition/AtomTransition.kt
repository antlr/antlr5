package org.antlr.v5.runtime.core.transition

import org.antlr.v5.runtime.core.misc.IntervalSet
import org.antlr.v5.runtime.core.state.ATNState

/**
 * TODO: make all transitions sets? No, should remove set edges.
 *
 * @param label The token type or character value. Or, signifies special label
 */
public class AtomTransition(target: ATNState, public val label: Int) : Transition(target) {
  override val serializationType: Int =
    ATOM

  override fun label(): IntervalSet =
    IntervalSet.of(label)

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    label == symbol

  override fun toString(): String =
    label.toString()
}