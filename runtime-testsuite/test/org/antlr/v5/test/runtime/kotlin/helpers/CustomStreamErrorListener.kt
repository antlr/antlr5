package org.antlr.v5.test.runtime.kotlin.helpers

import org.antlr.v5.runtime.kotlin.BaseErrorListener
import org.antlr.v5.runtime.core.error.RecognitionException
import org.antlr.v5.runtime.core.Recognizer
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
