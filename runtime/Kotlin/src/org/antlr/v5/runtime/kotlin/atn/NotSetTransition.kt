/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.atn

import org.antlr.v5.runtime.kotlin.misc.IntervalSet

public class NotSetTransition(target: ATNState, set: IntervalSet) : SetTransition(target, set) {
  override val serializationType: Int =
    NOT_SET

  override fun matches(symbol: Int, minVocabSymbol: Int, maxVocabSymbol: Int): Boolean =
    symbol in minVocabSymbol..maxVocabSymbol && !super.matches(symbol, minVocabSymbol, maxVocabSymbol)

  override fun toString(): String =
    "~${super.toString()}"
}
