package org.antlr.v5.test.runtime.kotlin.helpers

class RuntimeTestPrintStream(outStream: java.io.OutputStream) :
    java.io.PrintStream(outStream),
    com.strumenta.antlrkotlin.runtime.System.PrintStream