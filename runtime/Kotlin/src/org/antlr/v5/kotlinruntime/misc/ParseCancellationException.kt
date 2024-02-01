/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.kotlinruntime.misc

import org.antlr.v5.kotlinruntime.BailErrorStrategy
import org.antlr.v5.kotlinruntime.RecognitionException

/**
 * This exception is thrown to cancel a parsing operation. This exception does
 * not extend [RecognitionException], allowing it to bypass the standard
 * error recovery mechanisms. [BailErrorStrategy] throws this exception in
 * response to a parse error.
 *
 * @author Sam Harwell
 */
public class ParseCancellationException : RuntimeException {
  public constructor()
  public constructor(cause: Throwable) : super(cause)
  public constructor(message: String, cause: Throwable? = null) : super(message, cause)
}
