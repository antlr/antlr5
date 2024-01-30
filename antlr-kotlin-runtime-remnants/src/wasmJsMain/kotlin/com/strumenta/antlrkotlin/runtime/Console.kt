/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.kotlinruntime

internal actual fun platformPrintErrLn(): Unit =
  js("console.error('')")

internal actual fun platformPrintErrLn(message: String): Unit =
  js("console.error(message)")

internal actual fun platformPrintErr(message: String): Unit =
  js("console.error(message)")
