/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
@file:Suppress("NOTHING_TO_INLINE")

package com.strumenta.antlrkotlin.runtime

internal actual inline fun platformPrintErrLn(): Unit =
  console.error("")

internal actual inline fun platformPrintErrLn(message: String): Unit =
  console.error(message)

internal actual inline fun platformPrintErr(message: String): Unit =
  console.error(message)
