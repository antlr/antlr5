/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.core.state

/**
 * Start of `(A|B|...)+` loop.
 *
 * Technically a decision state, but we don't use for code generation.
 * Somebody might need it, so I'm defining it for completeness.
 *
 * In reality, the [PlusLoopbackState] node is the real decision-making note for `A+`.
 */
public class PlusBlockStartState : BlockStartState() {
  public var loopBackState: PlusLoopbackState? = null

  override val stateType: Int =
    PLUS_BLOCK_START
}
