/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package com.strumenta.antlrkotlin.runtime

internal actual fun platformGetEnv(name: String): String? {
  if (isNodeJs()) {
    return js("process.env[name]").unsafeCast<String?>()
  }

  System.out.println("Environment variables are not supported in the browser")
  return null
}
