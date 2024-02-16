/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.core.ast

/**
 * The Abstract Syntax Tree will be constituted by instances of [Node].
 */
public interface Node {
  public val parent: Node?
  public val position: Position?
}
