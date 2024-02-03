package org.antlr.v5.test.runtime.kotlin.helpers

import org.antlr.v5.runtime.kotlin.CharStream
import org.antlr.v5.runtime.kotlin.Lexer
import java.io.PrintStream

abstract class RuntimeTestLexer(input: CharStream) : Lexer(input) {
    var outStream: PrintStream = System.out
}
