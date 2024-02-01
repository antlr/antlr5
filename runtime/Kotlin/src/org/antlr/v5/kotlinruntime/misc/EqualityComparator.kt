/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.kotlinruntime.misc

/**
 * This interface provides an abstract concept of object equality independent of
 * [Any.equals] (object equality) and the `==` operator (reference equality).
 *
 * It can be used to provide algorithm-specific unordered
 * comparisons without requiring changes to the object itself.
 *
 * @author Sam Harwell
 */
public interface EqualityComparator<in T> {

  /**
   * This method returns a hash code for the specified object.
   *
   * @param obj The object
   * @return The hash code for [obj]
   */
  public fun hashCode(obj: T): Int

  /**
   * This method tests if two objects are equal.
   *
   * @param a The first object to compare
   * @param b The second object to compare
   * @return `true` if [a] equals [b], otherwise `false`
   */
  public fun equals(a: T?, b: T?): Boolean
}
