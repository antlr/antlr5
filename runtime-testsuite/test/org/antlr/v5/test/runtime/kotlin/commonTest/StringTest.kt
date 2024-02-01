/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class StringTest {
  @Test
  fun testAsCharArrayEmpty() {
    assertContentEquals(charArrayOf(), "".toCharArray())
  }

  @Test
  fun testAsCharArrayEmptyLength() {
    assertEquals(0, "".toCharArray().size)
  }

  @Test
  fun testAsCharArrayEmptyEl0() {
    assertEquals('a', "abc def".toCharArray()[0])
  }

  @Test
  fun testAsCharArray() {
    assertContentEquals(charArrayOf('a', 'b', 'c', ' ', 'd', 'e', 'f'), "abc def".toCharArray())
  }
}
