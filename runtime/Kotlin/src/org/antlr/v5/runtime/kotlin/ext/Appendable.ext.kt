/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin.ext

import org.antlr.v5.runtime.core.ext.toHex
import org.antlr.v5.runtime.core.misc.highSurrogate
import org.antlr.v5.runtime.core.misc.isBmpCodePoint
import org.antlr.v5.runtime.core.misc.isValidCodePoint
import org.antlr.v5.runtime.core.misc.lowSurrogate


/**
 * Appends the string representation of the [codePoint] argument to this sequence.
 *
 * See Java's `StringBuilder.appendCodePoint`.
 */
public fun <T : Appendable> T.appendCodePoint(codePoint: Int): T {
  if (isBmpCodePoint(codePoint)) {
    append(codePoint.toChar())
  } else if (isValidCodePoint(codePoint)) {
    append(highSurrogate(codePoint))
    append(lowSurrogate(codePoint))
  } else {
    throw IllegalArgumentException("Not a valid Unicode code point: ${codePoint.toHex()}")
  }

  return this
}
