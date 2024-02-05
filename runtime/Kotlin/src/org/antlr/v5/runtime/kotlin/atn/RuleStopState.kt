/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.atn

/**
 * The last node in the ATN for a rule, unless that rule is the start symbol.
 *
 * In that case, there is one transition to `EOF`. Later, we might encode
 * references to all calls to this rule to compute `FOLLOW` sets for
 * error handling.
 */
public class RuleStopState : ATNState() {
  override val stateType: Int =
    RULE_STOP
}
