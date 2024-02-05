/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin.ast

/**
 * Both the [start] point and the [end] point are inclusive.
 */
public data class Position(val start: Point, val end: Point) {
  public constructor(
    startLine: Int,
    startCol: Int,
    endLine: Int,
    endCol: Int,
  ) : this(Point(startLine, startCol), Point(endLine, endCol))

  init {
    require(start.isBefore(end) || start == end) {
      "End should follows start or be the same as start (start: $start, end: $end)"
    }
  }

  /**
   * Given the whole code, extract the portion of text corresponding to this position.
   */
  public fun text(wholeText: String): String =
    wholeText.substring(start.offset(wholeText), end.offset(wholeText))

  public fun length(code: String): Int =
    end.offset(code) - start.offset(code)

  public fun contains(point: Point): Boolean =
    ((point == start || start.isBefore(point)) && (point == end || point.isBefore(end)))
}
