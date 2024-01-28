package org.antlr.v5.test.runtime.kotlin.helpers

import org.antlr.v5.kotlinruntime.CharStream
import org.antlr.v5.kotlinruntime.Lexer
import java.io.PrintStream

abstract class RuntimeTestLexer(input: CharStream) : Lexer(input) {
    var outStream: PrintStream = System.out
}
