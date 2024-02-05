/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.atn

/**
 * The start of a regular `(...)` block.
 */
public abstract class BlockStartState : DecisionState() {
  public var endState: BlockEndState? = null
}
