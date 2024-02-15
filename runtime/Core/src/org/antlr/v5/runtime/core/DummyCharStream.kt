/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.core

import org.antlr.v5.runtime.core.misc.Interval

/**
 * To be used internally instead of a `null` assignment to a [CharStream] property.
 *
 * @author Edoardo Luppi
 */
internal object DummyCharStream : CharStream {
  override val sourceName: String
    get() = throw UnsupportedOperationException()

  override fun getText(interval: Interval): String =
    throw UnsupportedOperationException()

  override fun consume() =
    throw UnsupportedOperationException()

  override fun LA(i: Int): Int =
    throw UnsupportedOperationException()

  override fun mark(): Int =
    throw UnsupportedOperationException()

  override fun release(marker: Int): Unit =
    throw UnsupportedOperationException()

  override fun index(): Int =
    throw UnsupportedOperationException()

  override fun seek(index: Int) {
    // Noop
  }

  override fun size(): Int =
    0
}
