/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.core.dfa

import org.antlr.v5.runtime.core.VocabularyImpl

public class LexerDFASerializer(dfa: DFA) : DFASerializer(dfa, VocabularyImpl.EMPTY_VOCABULARY) {
  protected override fun getEdgeLabel(i: Int): String {
    val buf = StringBuilder()
    buf.append("'")
    buf.appendCodePoint(i)
    buf.append("'")
    return buf.toString()
  }
}
