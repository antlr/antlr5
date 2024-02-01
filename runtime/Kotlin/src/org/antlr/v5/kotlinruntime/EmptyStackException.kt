/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.kotlinruntime

/**
 * Thrown to indicate that a stack is empty.
 */
public class EmptyStackException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
