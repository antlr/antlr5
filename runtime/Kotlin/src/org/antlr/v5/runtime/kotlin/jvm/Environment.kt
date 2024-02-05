/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin.jvm

import java.lang.System as JavaSystem

@Suppress("NOTHING_TO_INLINE")
internal inline fun platformGetEnv(name: String): String? =
  JavaSystem.getenv(name)
