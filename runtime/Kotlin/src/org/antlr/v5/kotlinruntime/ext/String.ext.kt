/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.kotlinruntime.ext

public fun String.codePointIndices(): IntArray {
  val intArray = IntArray(length)
  var p = 0

  for (i in indices) {
    if (!hasSurrogatePairAt(i - 1)) {
      intArray[p++] = i
    }
  }

  return intArray.copyOfRange(0, p)
}
