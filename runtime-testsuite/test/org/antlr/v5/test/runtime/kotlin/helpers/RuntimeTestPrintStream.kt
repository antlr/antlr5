package org.antlr.v5.test.runtime.kotlin.helpers

import org.antlr.v5.kotlinruntime.System

class RuntimeTestPrintStream(outStream: java.io.OutputStream) :
    java.io.PrintStream(outStream),
    System.PrintStream