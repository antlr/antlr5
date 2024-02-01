/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package com.strumenta.antlrkotlin.runtime.ext

// See Java's Arrays.binarySearch0
public fun IntArray.binarySearch(element: Int, fromIndex: Int = 0, toIndex: Int = size): Int {
  rangeCheck(size, fromIndex, toIndex)

  var low = fromIndex
  var high = toIndex - 1

  while (low <= high) {
    val mid = (low + high) ushr 1
    val midVal = this[mid]

    if (midVal < element) {
      low = mid + 1
    } else if (midVal > element) {
      high = mid - 1
    } else {
      return mid // key found
    }
  }

  return -(low + 1) // key not found
}

private fun rangeCheck(size: Int, fromIndex: Int, toIndex: Int) {
  when {
    fromIndex > toIndex -> throw IllegalArgumentException("fromIndex($fromIndex) > toIndex($toIndex)")
    fromIndex < 0 -> throw IndexOutOfBoundsException("Array index out of range: $fromIndex")
    toIndex > size -> throw IndexOutOfBoundsException("Array index out of range: $toIndex")
  }
}
