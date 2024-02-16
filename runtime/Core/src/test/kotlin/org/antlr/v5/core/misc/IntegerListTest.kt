/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.core.misc

import org.antlr.v5.runtime.core.misc.IntegerList
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class IntegerListTest {
  @Test
  fun addToIntegerList() {
    val il = IntegerList()
    il.add(10)

    assertEquals(1, il.size())
    assertEquals(10, il[0])
  }

  @Test
  fun removeAtIntegerList() {
    val il = IntegerList()
    il.add(10)
    il.add(12)
    il.add(14)
    il.add(16)
    il.removeAt(2)

    assertEquals(3, il.size())
    assertEquals(10, il[0])
    assertEquals(12, il[1])
    assertEquals(16, il[2])
  }
}
