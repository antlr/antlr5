package org.antlr.v5.runtime.core.state

import org.antlr.v5.runtime.core.atn.ATN
import org.antlr.v5.runtime.core.misc.IntervalSet
import org.antlr.v5.runtime.core.transition.Transition


/**
 * The following images show the relation of states and
 * [ATNState.transitions] for various grammar constructs.
 *
 * - Solid edges marked with an &#0949; indicate a required [EpsilonTransition]
 * - Dashed edges indicate locations where any transition derived from [Transition] might appear
 * - Dashed nodes are placeholders for either a sequence of linked
 *   [BasicState] states or the inclusion of a block representing a nested
 *   construct in one of the forms below
 * - Nodes showing multiple outgoing alternatives with a `...` support
 *   any number of alternatives (one or more). Nodes without the `...` only
 *   support the exact number of alternatives shown in the diagram
 *
 * TODO(Edoardo): add missing piece of documentation linking to images
 */
@Suppress("MemberVisibilityCanBePrivate")
public abstract class ATNState {
  public companion object {
    public const val INITIAL_NUM_TRANSITIONS: Int = 4

    // Constants for serialization
    public const val INVALID_TYPE: Int = 0
    public const val BASIC: Int = 1
    public const val RULE_START: Int = 2
    public const val BLOCK_START: Int = 3
    public const val PLUS_BLOCK_START: Int = 4
    public const val STAR_BLOCK_START: Int = 5
    public const val TOKEN_START: Int = 6
    public const val RULE_STOP: Int = 7
    public const val BLOCK_END: Int = 8
    public const val STAR_LOOP_BACK: Int = 9
    public const val STAR_LOOP_ENTRY: Int = 10
    public const val PLUS_LOOP_BACK: Int = 11
    public const val LOOP_END: Int = 12

    public val serializationNames: List<String> = listOf(
      "INVALID",
      "BASIC",
      "RULE_START",
      "BLOCK_START",
      "PLUS_BLOCK_START",
      "STAR_BLOCK_START",
      "TOKEN_START",
      "RULE_STOP",
      "BLOCK_END",
      "STAR_LOOP_BACK",
      "STAR_LOOP_ENTRY",
      "PLUS_LOOP_BACK",
      "LOOP_END"
    )

    public const val INVALID_STATE_NUMBER: Int = -1
  }

  /**
   * Which ATN are we in?
   */
  public var atn: ATN? = null
  public var stateNumber: Int = INVALID_STATE_NUMBER
  public var ruleIndex: Int = 0 // at runtime, we don't have Rule objects
  public var epsilonOnlyTransitions: Boolean? = null

  /**
   * Track the transitions emanating from this ATN state.
   */
  public val transitions: MutableList<Transition> = ArrayList(INITIAL_NUM_TRANSITIONS)

  /**
   * Used to cache lookahead during parsing, not used during construction.
   */
  public var nextTokenWithinRule: IntervalSet? = null

  public val isNonGreedyExitState: Boolean =
    false

  public val numberOfTransitions: Int
    get() = transitions.size

  public abstract val stateType: Int

  override fun hashCode(): Int =
    stateNumber

  override fun equals(other: Any?): Boolean =
    // Are these states same object?
    other is ATNState && stateNumber == other.stateNumber

  override fun toString(): String =
    stateNumber.toString()

  public fun getTransitionsArray(): Array<Transition> =
    transitions.toTypedArray()

  public fun addTransition(e: Transition): Unit =
    addTransition(transitions.size, e)

  public fun addTransition(index: Int, e: Transition) {
      if(epsilonOnlyTransitions != null && epsilonOnlyTransitions != e.isEpsilon) {
          System.err.println("ATN state $stateNumber has both epsilon and non-epsilon transitions.")
      }
      var alreadyPresent = false

    for (t in transitions) {
      if (t.target.stateNumber == e.target.stateNumber) {
        if (t.label() != null && e.label() != null && t.label()!! == e.label()) {
          alreadyPresent = true
          break
        } else if (t.isEpsilon && e.isEpsilon) {
          alreadyPresent = true
          break
        }
      }
    }

    if (!alreadyPresent) {
      transitions.add(index, e)
        recalculateEpsilonOnlyTransitions();
    }
  }

  public fun transition(i: Int): Transition =
    transitions[i]

    fun getTransitionIndex(transition: Transition?): Int {
        return transitions.indexOf(transition)
    }

     public fun setTransition(i: Int, e: Transition) {
        transitions.removeAt(i);
        recalculateEpsilonOnlyTransitions();
        if (epsilonOnlyTransitions != null && epsilonOnlyTransitions != e.isEpsilon) {
            System.err.println("ATN state $stateNumber has both epsilon and non-epsilon transitions.\n");
        }
        transitions.add(i, e);
        recalculateEpsilonOnlyTransitions();
  }

  public fun removeTransition(index: Int): Transition  {
      val result = transitions.removeAt(index)
      recalculateEpsilonOnlyTransitions()
      return result

  }

    public fun removeTransition(transition: Transition): Boolean {
        val result = transitions.remove(transition)
        recalculateEpsilonOnlyTransitions()
        return result
    }

    fun findTransition(predicate: java.util.function.Predicate<Transition?>?): Transition? {
        return transitions.stream().filter(predicate).findFirst().orElse(null)
    }



    public fun clearTransitions() {
        transitions.clear()
    }
  public fun onlyHasEpsilonTransitions(): Boolean =
      epsilonOnlyTransitions != null && epsilonOnlyTransitions!!

    fun recalculateEpsilonOnlyTransitions() {
        if (transitions.size == 0) {
            epsilonOnlyTransitions = null
        } else {
            epsilonOnlyTransitions = transitions.stream().allMatch(Transition::isEpsilon)
        }
    }
}