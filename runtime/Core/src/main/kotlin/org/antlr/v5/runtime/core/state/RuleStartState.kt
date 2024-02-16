/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.core.state

public class RuleStartState : ATNState() {
  public var stopState: RuleStopState? = null
  public var isLeftRecursiveRule: Boolean = false

  override val stateType: Int =
    RULE_START
}
