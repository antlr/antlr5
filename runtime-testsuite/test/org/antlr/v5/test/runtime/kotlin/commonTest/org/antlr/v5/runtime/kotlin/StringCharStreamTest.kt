/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.test.runtime.kotlin.commonTest.org.antlr.v5.kotlinruntime

import org.antlr.v5.runtime.kotlin.ext.codePointIndices
import org.antlr.v5.runtime.kotlin.jvm.CharStreams
import org.antlr.v5.runtime.kotlin.misc.Interval
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class StringCharStreamTest {
  @Test
  fun testCodePointIndices8() {
    val expected = intArrayOf(0, 1)
    val actual = "ab".codePointIndices()
    assertContentEquals(expected, actual)
  }

  @Test
  fun testCodePointIndices16() {
    val expected = intArrayOf(0, 1, 2)
    val actual = "aΔb".codePointIndices()
    assertContentEquals(expected, actual)
  }

  @Test
  fun testCodePointIndices32() {
    val expected = intArrayOf(0, 1, 3, 4, 5, 6, 8)
    val actual = "a😱bΔc😱d".codePointIndices()
    assertContentEquals(expected, actual)
  }

  @Test
  fun testSize8() {
    val expected = 2
    val actual = CharStreams.fromString("ab").size()
    assertEquals(expected, actual)
  }

  @Test
  fun testSize16() {
    val expected = 3
    val actual = CharStreams.fromString("aΔb").size()
    assertEquals(expected, actual)
  }

  @Test
  fun testSize32() {
    val expected = 3
    val actual = CharStreams.fromString("a😱b").size()
    assertEquals(expected, actual)
  }

  @Test
  fun testGetText8() {
    val expected = "cde"
    val actual = CharStreams.fromString("abcdef").getText(Interval.of(2, 4))
    assertEquals(expected, actual)
  }

  @Test
  fun testGetText16() {
    val expected = "cΔe"
    val actual = CharStreams.fromString("abcΔef").getText(Interval.of(2, 4))
    assertEquals(expected, actual)
  }

  @Test
  fun testGetText32() {
    val expected = "c😱e"
    val actual = CharStreams.fromString("abc😱ef").getText(Interval.of(2, 4))
    assertEquals(expected, actual)
  }
}
