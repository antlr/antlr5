/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.kotlinruntime.tree.xpath

import org.antlr.v5.kotlinruntime.BaseErrorListener
import org.antlr.v5.kotlinruntime.RecognitionException
import org.antlr.v5.kotlinruntime.Recognizer

public open class XPathLexerErrorListener : BaseErrorListener() {
  override fun syntaxError(
    recognizer: Recognizer<*, *>,
    offendingSymbol: Any?,
    line: Int,
    charPositionInLine: Int,
    msg: String,
    e: RecognitionException?,
  ) {
    // Noop
  }
}
