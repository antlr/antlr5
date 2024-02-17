/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin._unused.tree.pattern

import org.antlr.v5.runtime.core.CommonToken
import org.antlr.v5.runtime.core.Token

/**
 * A [Token] object representing a token of a particular type; e.g.,
 * `<ID>`. These tokens are created for [TagChunk] chunks where the
 * tag corresponds to a lexer rule or token type.
 *
 * @param tokenName The token name
 * @param type The token type
 * @param label The label associated with the token tag, or `null` if the token tag is unlabeled
 */
@Suppress("MemberVisibilityCanBePrivate")
public class TokenTagToken(
  public val tokenName: String, type: Int,
  public val label: String? = null,
) : CommonToken(type) {
  /**
   * The implementation for [TokenTagToken] returns the token tag formatted with `<` and `>` delimiters.
   */
  @Suppress("SuspiciousVarProperty")
  override var text: String? = null
    get() = if (label != null) {
      "<$label:$tokenName>"
    } else {
      "<$tokenName>"
    }

  /**
   * The implementation for [TokenTagToken] returns a string of the form `tokenName:type`.
   */
  override fun toString(): String =
    "$tokenName:$type"
}
