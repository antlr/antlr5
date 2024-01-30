/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.kotlinruntime

@Suppress("UNUSED_PARAMETER", "RedundantNullableReturnType")
private fun processEnvJs(name: String): String? =
  js("process.env[name]")

internal actual fun platformGetEnv(name: String): String? {
  if (isNodeJs()) {
    return processEnvJs(name)
  }

  System.out.println("Environment variables are not supported in the browser")
  return null
}
