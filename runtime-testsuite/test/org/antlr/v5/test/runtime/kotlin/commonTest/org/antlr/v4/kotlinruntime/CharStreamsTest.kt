/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.test.runtime.kotlin.commonTest.org.antlr.v5.kotlinruntime

import org.antlr.v5.kotlinruntime.CharStreams
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CharStreamsTest {
  @Test
  fun testFromString8() {
    val stream = CharStreams.fromString("ab")
    assertEquals(2, stream.size())
    assertEquals(0, stream.index())

    assertEquals(-1, stream.LA(-3))
    assertEquals(-1, stream.LA(-2))
    assertEquals(-1, stream.LA(-1))
    assertEquals(0, stream.LA(0))
    assertEquals(97, stream.LA(1))
    assertEquals(98, stream.LA(2))
    assertEquals(-1, stream.LA(3))

    stream.consume()

    assertEquals(1, stream.index())

    assertEquals(-1, stream.LA(-3))
    assertEquals(-1, stream.LA(-2))
    assertEquals(97, stream.LA(-1))
    assertEquals(0, stream.LA(0))
    assertEquals(98, stream.LA(1))
    assertEquals(-1, stream.LA(2))
    assertEquals(-1, stream.LA(3))

    stream.consume()

    assertEquals(2, stream.index())

    assertEquals(-1, stream.LA(-3))
    assertEquals(97, stream.LA(-2))
    assertEquals(98, stream.LA(-1))
    assertEquals(0, stream.LA(0))
    assertEquals(-1, stream.LA(1))
    assertEquals(-1, stream.LA(2))
    assertEquals(-1, stream.LA(3))
  }

  @Test
  fun testFromString16() {
    val stream = CharStreams.fromString("aΔ")
    assertEquals(2, stream.size())
    assertEquals(0, stream.index())

    assertEquals(-1, stream.LA(-3))
    assertEquals(-1, stream.LA(-2))
    assertEquals(-1, stream.LA(-1))
    assertEquals(0, stream.LA(0))
    assertEquals(97, stream.LA(1))
    assertEquals(916, stream.LA(2))
    assertEquals(-1, stream.LA(3))

    stream.consume()

    assertEquals(1, stream.index())

    assertEquals(-1, stream.LA(-3))
    assertEquals(-1, stream.LA(-2))
    assertEquals(97, stream.LA(-1))
    assertEquals(0, stream.LA(0))
    assertEquals(916, stream.LA(1))
    assertEquals(-1, stream.LA(2))
    assertEquals(-1, stream.LA(3))

    stream.consume()

    assertEquals(2, stream.index())

    assertEquals(-1, stream.LA(-3))
    assertEquals(97, stream.LA(-2))
    assertEquals(916, stream.LA(-1))
    assertEquals(0, stream.LA(0))
    assertEquals(-1, stream.LA(1))
    assertEquals(-1, stream.LA(2))
    assertEquals(-1, stream.LA(3))
  }

  @Test
  fun testFromString32() {
    /**
     * 😱 is encoded as:
     * UTF-8:	0xF0 0x9F 0x98 0xB1
     * UTF-16:	0xD83D 0xDE31
     * UTF-32:	0x0001F631
     */
    val stream = CharStreams.fromString("a😱b")
    assertEquals(3, stream.size())
    assertEquals(0, stream.index())

    assertEquals(-1, stream.LA(-4))
    assertEquals(-1, stream.LA(-3))
    assertEquals(-1, stream.LA(-2))
    assertEquals(-1, stream.LA(-1))
    assertEquals(0, stream.LA(0))
    assertEquals(97, stream.LA(1))
    assertEquals(0x1F631, stream.LA(2))
    assertEquals(98, stream.LA(3))
    assertEquals(-1, stream.LA(4))

    stream.consume()

    assertEquals(1, stream.index())

    assertEquals(-1, stream.LA(-4))
    assertEquals(-1, stream.LA(-3))
    assertEquals(-1, stream.LA(-2))
    assertEquals(97, stream.LA(-1))
    assertEquals(0, stream.LA(0))
    assertEquals(0x1F631, stream.LA(1))
    assertEquals(98, stream.LA(2))
    assertEquals(-1, stream.LA(3))
    assertEquals(-1, stream.LA(4))

    stream.consume()

    assertEquals(2, stream.index())

    assertEquals(-1, stream.LA(-4))
    assertEquals(-1, stream.LA(-3))
    assertEquals(97, stream.LA(-2))
    assertEquals(0x1F631, stream.LA(-1))
    assertEquals(0, stream.LA(0))
    assertEquals(98, stream.LA(1))
    assertEquals(-1, stream.LA(2))
    assertEquals(-1, stream.LA(3))
    assertEquals(-1, stream.LA(4))

    stream.consume()

    assertEquals(3, stream.index())

    assertEquals(-1, stream.LA(-4))
    assertEquals(97, stream.LA(-3))
    assertEquals(0x1F631, stream.LA(-2))
    assertEquals(98, stream.LA(-1))
    assertEquals(0, stream.LA(0))
    assertEquals(-1, stream.LA(1))
    assertEquals(-1, stream.LA(2))
    assertEquals(-1, stream.LA(3))
    assertEquals(-1, stream.LA(4))
  }
}
