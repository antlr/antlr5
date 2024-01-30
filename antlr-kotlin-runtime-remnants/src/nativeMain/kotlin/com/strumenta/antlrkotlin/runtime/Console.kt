/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package org.antlr.v5.kotlinruntime

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.internal.GCUnsafeCall
import kotlin.native.internal.InternalForKotlinNative

@OptIn(ExperimentalNativeApi::class)
private val lineBreak = if (Platform.osFamily == OsFamily.WINDOWS) "\r\n" else "\n"

@OptIn(InternalForKotlinNative::class)
@GCUnsafeCall("Kotlin_io_Console_printToStdErr")
private external fun runtimePrintToStdErr(message: String)

internal actual inline fun platformPrintErrLn(): Unit =
  runtimePrintToStdErr(lineBreak)

internal actual inline fun platformPrintErrLn(message: String): Unit =
  runtimePrintToStdErr("$message$lineBreak")

internal actual inline fun platformPrintErr(message: String): Unit =
  runtimePrintToStdErr(message)
