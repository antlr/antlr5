package org.antlr.v5.runtime.core.transition

import org.antlr.v5.runtime.core.state.ATNState
import org.antlr.v5.runtime.core.state.RuleStartState

/**
 * @param ruleIndex Ptr to the rule definition object for this rule rer
 * @param followState What node to begin computations following ref to rule
 */
public class RuleTransition(
    ruleStart: RuleStartState,
    public val ruleIndex: Int,
    public val precedence: Int,
    public var followState: ATNState,
) : Transition(ruleStart) {
  override val serializationType: Int =
    RULE

  override val isEpsilon: Boolean =
    true

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    false
}