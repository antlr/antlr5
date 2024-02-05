/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.atn

public class StarLoopbackState : ATNState() {
  public val loopEntryState: StarLoopEntryState
    get() = transition(0).target as StarLoopEntryState

  override val stateType: Int =
    STAR_LOOP_BACK
}
