/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.atn

/**
 * Terminal node of a simple `(a|b|c)` block.
 */
public class BlockEndState : ATNState() {
  public var startState: BlockStartState? = null

  override val stateType: Int =
    BLOCK_END
}
