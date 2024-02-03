/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin

import kotlin.jvm.JvmOverloads

public abstract class AbstractCharStreams {
  /**
   * Creates a [CharStream] given a string and the [sourceName] from which it came.
   */
  @JvmOverloads
  public fun fromString(str: String, sourceName: String = IntStream.UNKNOWN_SOURCE_NAME): CharStream =
    StringCharStream(str, sourceName)
}
