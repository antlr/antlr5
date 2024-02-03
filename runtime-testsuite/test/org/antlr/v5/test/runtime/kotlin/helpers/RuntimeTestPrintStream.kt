package org.antlr.v5.test.runtime.kotlin.helpers

import org.antlr.v5.runtime.kotlin.System

class RuntimeTestPrintStream(outStream: java.io.OutputStream) :
    java.io.PrintStream(outStream),
    System.PrintStream