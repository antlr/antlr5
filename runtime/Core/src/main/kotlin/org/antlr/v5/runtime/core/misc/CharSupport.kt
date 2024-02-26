/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.core.misc

import org.antlr.v5.runtime.core.Lexer

object CharSupport {
    val EscapedCharValue: MutableMap<Char, Char> = HashMap()

    val CharValueEscape: MutableMap<Char, String> = HashMap()

    init {
        EscapedCharValue['n'] = '\n'
        EscapedCharValue['r'] = '\r'
        EscapedCharValue['t'] = '\t'
        EscapedCharValue['b'] = '\b'
        EscapedCharValue['f'] = 0x0C.toChar()
        EscapedCharValue['\\'] = '\\'
        CharValueEscape['\n'] = "\\n"
        CharValueEscape['\r'] = "\\r"
        CharValueEscape['\t'] = "\\t"
        CharValueEscape['\b'] = "\\b"
        CharValueEscape[0x0C.toChar()] = "\\f"
        CharValueEscape['\\'] = "\\\\"
    }

    fun getPrintable(c: Int): String {
        return getPrintable(c, true)
    }

    /** Return a string representing the escaped char for code c.  E.g., If c
     * has value 0x100, you will get "\\u0100".  ASCII gets the usual
     * char (non-hex) representation.  Non-ASCII characters are spit out
     * as \\uXXXX or \\u{XXXXXX} escapes.
     */
    fun getPrintable(c: Int, appendQuotes: Boolean): String {
        val result: String
        if (c < Lexer.MIN_CHAR_VALUE) {
            result = "<INVALID>"
        } else {
            val charValueEscape = CharValueEscape[c.toChar()]
            result = charValueEscape
                ?: if (Character.UnicodeBlock.of(c.toChar()) === Character.UnicodeBlock.BASIC_LATIN &&
                    !Character.isISOControl(c.toChar())
                ) {
                    if (c == '\\'.code) {
                        "\\\\"
                    } else if (c == '\''.code) {
                        "\\'"
                    } else {
                        Character.toString(c.toChar())
                    }
                } else if (c <= 0xFFFF) {
                    String.format("\\u%04X", c)
                } else {
                    String.format("\\u{%06X}", c)
                }
        }
        if (appendQuotes) {
            return "'$result'"
        }
        return result
    }
}