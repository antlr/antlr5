/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.atn

import org.antlr.v5.runtime.kotlin.misc.IntervalSet

public class RangeTransition(target: ATNState, public val from: Int, public val to: Int) : Transition(target) {
  override val serializationType: Int =
    RANGE

  override fun label(): IntervalSet =
    IntervalSet.of(from, to)

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    symbol in from..to

  override fun toString(): String {
    val buf = StringBuilder("'")
    buf.appendCodePoint(from)
    buf.append("'..'")
    buf.appendCodePoint(to)
    buf.append("'")
    return buf.toString()
  }
}
