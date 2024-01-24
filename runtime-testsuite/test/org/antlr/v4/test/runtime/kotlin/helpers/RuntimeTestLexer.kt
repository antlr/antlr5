package org.antlr.v4.test.runtime.kotlin.helpers

import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.Lexer
import java.io.PrintStream

abstract class RuntimeTestLexer(input: CharStream) : Lexer(input) {
    var outStream: PrintStream = System.out
}
