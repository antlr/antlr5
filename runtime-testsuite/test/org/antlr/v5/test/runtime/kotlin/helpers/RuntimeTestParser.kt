package org.antlr.v5.test.runtime.kotlin.helpers

import org.antlr.v5.runtime.core.TokenStream
import org.antlr.v5.runtime.core.Parser

abstract class RuntimeTestParser(input: TokenStream) : Parser(input) {
    var outStream: RuntimeTestPrintStream = RuntimeTestPrintStream(System.out)
}
