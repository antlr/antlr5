/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin._unused.atn

import org.antlr.v5.runtime.core.state.ATNState
import org.antlr.v5.runtime.core.transition.AtomTransition
import org.antlr.v5.runtime.core.transition.RangeTransition
import org.antlr.v5.runtime.core.transition.Transition

/**
 * Utility class to create [AtomTransition], [RangeTransition],
 * and [SetTransition] appropriately based on the range of the input.
 *
 * Previously, we distinguished between atom and range transitions for
 * Unicode code points <= `U+FFFF` and those above. We used a set
 * transition for a Unicode code point > `U+FFFF`. Now that we can serialize
 * 32-bit int/chars in the ATN serialization, this is no longer necessary.
 */
@Suppress("MemberVisibilityCanBePrivate")
public object CodePointTransitions {
  /**
   * Return new [AtomTransition].
   */
  public fun createWithCodePoint(target: ATNState, codePoint: Int): Transition =
    createWithCodePointRange(target, codePoint, codePoint)

  /**
   * Return new [AtomTransition] if range represents one atom, else [SetTransition].
   */
  public fun createWithCodePointRange(target: ATNState, codePointFrom: Int, codePointTo: Int): Transition =
    if (codePointFrom == codePointTo) {
      AtomTransition(target, codePointFrom)
    } else {
      RangeTransition(target, codePointFrom, codePointTo)
    }
}
