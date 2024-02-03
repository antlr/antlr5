/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.atn

import org.antlr.v5.runtime.kotlin.ParserInterpreter
import org.antlr.v5.runtime.kotlin.dfa.DFA

public class StarLoopEntryState : DecisionState() {
  public var loopBackState: StarLoopbackState? = null

  /**
   * Indicates whether this state can benefit from a precedence DFA during SLL
   * decision-making.
   *
   * This is a computed property that is calculated during ATN deserialization
   * and stored for use in [ParserATNSimulator] and [ParserInterpreter].
   *
   * @see DFA.isPrecedenceDfa
   */
  public var isPrecedenceDecision: Boolean = false

  override val stateType: Int =
    STAR_LOOP_ENTRY
}
