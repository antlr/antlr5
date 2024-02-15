/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.core.misc

public object Collections {
  public fun <T : Comparable<T>> min(collection: Collection<T>): T =
    collection.minOrNull() ?: throw NoSuchElementException()

  public fun <T : Comparable<T>> max(collection: Collection<T>): T =
    collection.maxOrNull() ?: throw NoSuchElementException()
}
