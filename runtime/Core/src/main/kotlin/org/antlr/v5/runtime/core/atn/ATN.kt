package org.antlr.v5.runtime.core.atn

import org.antlr.v5.runtime.core.Token
import org.antlr.v5.runtime.core.action.LexerAction
import org.antlr.v5.runtime.core.context.RuleContext
import org.antlr.v5.runtime.core.misc.IntervalSet
import org.antlr.v5.runtime.core.state.*
import org.antlr.v5.runtime.core.transition.RuleTransition

/**
 * Used for runtime deserialization of ATNs from strings.
 *
 * @param grammarType The type of the ATN
 * @param maxTokenType The maximum value for any symbol recognized
 *   by a transition in the ATN
 */
public class ATN(public val grammarType: ATNType, public val maxTokenType: Int) {
  public companion object {
    public const val INVALID_ALT_NUMBER: Int = 0
  }

  public val states: MutableList<ATNState?> = ArrayList()

  /**
   * Each sub-rule/rule is a decision point, and we must track them, so we
   * can go back later and build DFA predictors for them.
   *
   * This includes all the rules, sub-rules, optional blocks, `()+`, `()*`, etc.
   */
  public val decisionToState: MutableList<DecisionState> = ArrayList()

  /**
   * Maps from rule index to starting state number.
   */
  public var ruleToStartState: Array<RuleStartState>? = null

  /**
   * Maps from rule index to stop state number.
   */
  public var ruleToStopState: Array<RuleStopState?>? = null
  public val modeNameToStartState: Map<String, TokensStartState> = LinkedHashMap()

  /**
   * For lexer ATNs, this maps the rule index to the resulting token type.
   *
   * For parser ATNs, this maps the rule index to the generated bypass token
   * type if the [ATNDeserializationOptions.isGenerateRuleBypassTransitions]
   * deserialization option was specified, otherwise this is `null`.
   */
  public var ruleToTokenType: IntArray? = null

  /**
   * For lexer ATNs, this is an array of [LexerAction] objects which may
   * be referenced by action transitions in the ATN.
   */
  public var lexerActions: Array<LexerAction>? = null
  public val modeToStartState: MutableList<TokensStartState> = ArrayList()

  public val numberOfDecisions: Int
    get() = decisionToState.size

  /**
   * Compute the set of valid tokens that can occur starting in state [s].
   *
   * If [ctx] is `null`, the set of tokens will not include what can follow
   * the rule surrounding [s]. In other words, the set will be
   * restricted to tokens reachable staying within [s]'s rule.
   */
  public fun nextTokens(s: ATNState, ctx: RuleContext?): IntervalSet {
    val anal = LL1Analyzer(this)
    return anal.LOOK(s, ctx)
  }

  /**
   * Compute the set of valid tokens that can occur starting in [s] and
   * staying in same rule.
   *
   * [Token.EPSILON] is in set if we reach end of rule.
   */
  public fun nextTokens(s: ATNState): IntervalSet {
    var nextTokenWithinRule = s.nextTokenWithinRule

    if (nextTokenWithinRule != null) {
      return nextTokenWithinRule
    }

    nextTokenWithinRule = nextTokens(s, null)
    nextTokenWithinRule.isReadonly = true
    s.nextTokenWithinRule = nextTokenWithinRule
    return nextTokenWithinRule
  }

  public fun addState(state: ATNState?) {
    if (state != null) {
      state.atn = this
      state.stateNumber = states.size
    }

    states.add(state)
  }

  public fun removeState(state: ATNState): ATNState? {
    // Just free mem, don't shift states in list
    var removing = states[state.stateNumber]
    states[state.stateNumber] = null
      return removing
  }

  public fun defineDecisionState(s: DecisionState): Int {
    decisionToState.add(s)
    s.decision = decisionToState.size - 1
    return s.decision
  }

  public fun getDecisionState(decision: Int): DecisionState? =
    if (decisionToState.isNotEmpty()) {
      decisionToState[decision]
    } else {
      null
    }

  /**
   * Computes the set of input symbols which could follow ATN state number
   * [stateNumber] in the specified full [context].
   *
   * This method considers the complete parser context, but does not evaluate semantic
   * predicates (i.e., all predicates encountered during the calculation are
   * assumed true).
   *
   * If a path in the ATN exists from the starting state to the
   * [RuleStopState] of the outermost context without matching any
   * symbols, [Token.EOF] is added to the returned set.
   *
   * If [context] is `null`, it is treated as [ParserRuleContext.EMPTY].
   *
   * Note that this does NOT give you the set of all tokens that could
   * appear at a given token position in the input phrase. In other words,
   * it does not answer:
   *
   * > Given a specific partial input phrase, return the set of all tokens
   *   that can follow the last token in the input phrase
   *
   * The big difference is that with just the input, the parser could
   * land right in the middle of a lookahead decision. Getting
   * all *possible* tokens given a partial input stream is a separate
   * computation. See [antlr4#1428](https://github.com/antlr/antlr4/issues/1428).
   *
   * For this function, we are specifying an ATN state and call stack to compute
   * what token(s) can come next and specifically: outside a lookahead decision.
   * That is what you want for error reporting and recovery upon parse error.
   *
   * @param stateNumber The ATN state number
   * @param context The full parse context
   * @return The set of potentially valid input symbols which could follow the
   *   specified state in the specified context
   * @throws IllegalArgumentException If the ATN does not contain a state with
   *   number [stateNumber]
   */
  public fun getExpectedTokens(stateNumber: Int, context: RuleContext?): IntervalSet {
    if (stateNumber < 0 || stateNumber >= states.size) {
      throw IllegalArgumentException("Invalid state number.")
    }

    var ctx: RuleContext? = context
    val s = states[stateNumber]
    var following = nextTokens(s!!)

    if (!following.contains(Token.EPSILON)) {
      return following
    }

    val expected = IntervalSet()
    expected.addAll(following)
    expected.remove(Token.EPSILON)

    while (ctx != null && ctx.invokingState >= 0 && following.contains(Token.EPSILON)) {
      val invokingState = states[ctx.invokingState]
      val rt = invokingState!!.transition(0) as RuleTransition
      following = nextTokens(rt.followState)
      expected.addAll(following)
      expected.remove(Token.EPSILON)
      ctx = ctx.getParent()
    }

    if (following.contains(Token.EPSILON)) {
      expected.add(Token.EOF)
    }

    return expected
  }
}