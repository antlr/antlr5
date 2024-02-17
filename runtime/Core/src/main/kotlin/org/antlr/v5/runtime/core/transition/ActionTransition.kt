package org.antlr.v5.runtime.core.transition

import org.antlr.v5.runtime.core.state.ATNState

public class ActionTransition(
    target: ATNState,
    public val ruleIndex: Int,
    public val actionIndex: Int = -1,
    public val isCtxDependent: Boolean = false, // e.g., $i ref in action
) : Transition(target) {
  override val serializationType: Int =
    ACTION

  override val isEpsilon: Boolean =
    true

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    false

  override fun toString(): String =
    "action_$ruleIndex:$actionIndex"
}