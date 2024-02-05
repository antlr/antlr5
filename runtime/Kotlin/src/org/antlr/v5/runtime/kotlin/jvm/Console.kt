/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
@file:Suppress("NOTHING_TO_INLINE")

package org.antlr.v5.runtime.kotlin.jvm

import java.lang.System as JavaSystem

internal inline fun platformPrintErrLn(): Unit =
  JavaSystem.err.println()

internal inline fun platformPrintErrLn(message: String): Unit =
  JavaSystem.err.println(message)

internal inline fun platformPrintErr(message: String): Unit =
  JavaSystem.err.print(message)
