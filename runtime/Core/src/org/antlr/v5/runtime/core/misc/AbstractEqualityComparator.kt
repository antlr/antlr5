/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.core.misc

/**
 * This abstract base class is provided so performance-critical applications can
 * use virtual-dispatch instead of interface-dispatch when calling comparator methods.
 *
 * @author Sam Harwell
 */
public abstract class AbstractEqualityComparator<in T> : EqualityComparator<T>
