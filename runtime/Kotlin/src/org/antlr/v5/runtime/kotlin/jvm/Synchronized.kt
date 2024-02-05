/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin.jvm

public inline fun <R> synchronized(lock: Any, block: () -> R): R =
  kotlin.synchronized(lock, block)
