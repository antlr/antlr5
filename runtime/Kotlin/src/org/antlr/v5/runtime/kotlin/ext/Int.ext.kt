/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin.ext

@OptIn(ExperimentalStdlibApi::class)
private val hexFormat: HexFormat = HexFormat {
  upperCase = true
  number {
    prefix = "0x"
    removeLeadingZeros = true
  }
}

@OptIn(ExperimentalStdlibApi::class)
public fun Int.toHex(): String =
  toHexString(hexFormat)
