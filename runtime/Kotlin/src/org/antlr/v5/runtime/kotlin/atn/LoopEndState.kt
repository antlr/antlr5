/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.atn

/**
 * Mark the end of a * or + loop.
 */
public class LoopEndState : ATNState() {
  public var loopBackState: ATNState? = null

  override val stateType: Int =
    LOOP_END
}
