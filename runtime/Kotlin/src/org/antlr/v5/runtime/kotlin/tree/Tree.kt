/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.tree

import org.antlr.v5.runtime.kotlin.RuleContext
import org.antlr.v5.runtime.kotlin.Token

/**
 * The basic notion of a tree has a parent, a payload, and a list of children.
 * It is the most abstract interface for all the trees used by ANTLR.
 */
public interface Tree {
  /**
   * This method returns whatever object represents the data at this node.
   * For example, for parse trees, the payload can be a [Token] representing
   * a leaf node or a [RuleContext] object representing a rule
   * invocation. For abstract syntax trees (ASTs), this is a [Token] object.
   */
  public val payload: Any?

  /**
   * How many children are there? If there is none, then this
   * node represents a leaf node.
   */
  public val childCount: Int

  /**
   * The parent of this node.
   *
   * If the return value is `null`, then this node is the root of the tree.
   */
  public fun readParent(): Tree?

  /**
   * If there are children, get the `i`th value indexed from `0`.
   */
  public fun getChild(i: Int): Tree?

  /**
   * Print out a whole tree, not just a node, in LISP format
   * `(root child1 .. childN)`. Print just a node if this is a leaf.
   */
  public fun toStringTree(): String
}
