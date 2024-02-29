/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.core.misc

import org.antlr.v5.runtime.core.misc.CharSupport
import org.antlr.v5.runtime.core.misc.IntervalSet
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestCharSupport {
    @Test
    fun testGetPrintable() {
        Assertions.assertEquals("'<INVALID>'", CharSupport.getPrintable(-1))
        Assertions.assertEquals("'\\n'", CharSupport.getPrintable('\n'.code))
        Assertions.assertEquals("'\\\\'", CharSupport.getPrintable('\\'.code))
        Assertions.assertEquals("'\\''", CharSupport.getPrintable('\''.code))
        Assertions.assertEquals("'b'", CharSupport.getPrintable('b'.code))
        Assertions.assertEquals("'\\uFFFF'", CharSupport.getPrintable(0xFFFF))
        Assertions.assertEquals("'\\u{10FFFF}'", CharSupport.getPrintable(0x10FFFF))
    }

    @Test
    fun testGetIntervalSetEscapedString() {
        Assertions.assertEquals("{}", IntervalSet().toString(true))
        Assertions.assertEquals("'\\u0000'", IntervalSet(0).toString(true))
        Assertions.assertEquals("{'\\u0001'..'\\u0003'}", IntervalSet(3, 1, 2).toString(true))
    }
}