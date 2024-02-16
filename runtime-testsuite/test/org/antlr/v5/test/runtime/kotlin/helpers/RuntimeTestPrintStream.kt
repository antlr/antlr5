package org.antlr.v5.test.runtime.kotlin.helpers

import org.antlr.v5.runtime.core.IPrintStream

class RuntimeTestPrintStream(outStream: java.io.OutputStream) :
    java.io.PrintStream(outStream),
    IPrintStream {
    override fun printLine(value: String?) {
        super.println(value)
    }

}