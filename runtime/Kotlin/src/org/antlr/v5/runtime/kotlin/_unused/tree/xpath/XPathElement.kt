/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin._unused.tree.xpath

import org.antlr.v5.runtime.core.tree.ParseTree

@Suppress("MemberVisibilityCanBePrivate")
public abstract class XPathElement(protected var nodeName: String) {
  public var invert: Boolean = false

  /**
   * Given tree rooted at [t], returns all nodes matched by this path element.
   */
  public abstract fun evaluate(t: ParseTree): Collection<ParseTree>

  override fun toString(): String {
    val inv = if (invert) "!" else ""
    val className = this::class.simpleName
    return "$className[$inv$nodeName]"
  }
}
