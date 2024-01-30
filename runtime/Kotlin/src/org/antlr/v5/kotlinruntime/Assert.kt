/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.kotlinruntime

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Throws an [AssertionError] if the [condition] is `false`.
 */
@OptIn(ExperimentalContracts::class)
@Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")
public inline fun assert(condition: Boolean, message: String? = null) {
  contract {
    returns() implies condition
  }

  if (!condition) {
    throw AssertionError(message)
  }
}
