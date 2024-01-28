package org.antlr.v5.test.runtime.kotlin.helpers

import org.antlr.v5.kotlinruntime.BaseErrorListener
import org.antlr.v5.kotlinruntime.RecognitionException
import org.antlr.v5.kotlinruntime.Recognizer
import java.io.PrintStream

class CustomStreamErrorListener(private val printStream: PrintStream) : BaseErrorListener() {
    override fun syntaxError(
        recognizer: Recognizer<*, *>,
        offendingSymbol: Any?,
        line: Int,
        charPositionInLine: Int,
        msg: String,
        e: RecognitionException?
    ) {
        printStream.println("line $line:$charPositionInLine $msg")
    }
}
