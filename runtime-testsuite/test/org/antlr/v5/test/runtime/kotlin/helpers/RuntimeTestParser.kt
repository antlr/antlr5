package org.antlr.v5.test.runtime.kotlin.helpers

import org.antlr.v5.kotlinruntime.Parser
import org.antlr.v5.kotlinruntime.TokenStream

abstract class RuntimeTestParser(input: TokenStream) : Parser(input) {
    var outStream: RuntimeTestPrintStream = RuntimeTestPrintStream(System.out)
}
