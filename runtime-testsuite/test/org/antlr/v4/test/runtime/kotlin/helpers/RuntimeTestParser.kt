package org.antlr.v4.test.runtime.kotlin.helpers

import org.antlr.v4.kotlinruntime.Parser
import org.antlr.v4.kotlinruntime.TokenStream

abstract class RuntimeTestParser(input: TokenStream) : Parser(input) {
    var outStream: RuntimeTestPrintStream = RuntimeTestPrintStream(System.out)
}
