package org.antlr.v5.test.runtime.java.helpers;

import org.antlr.v5.runtime.core.Parser;
import org.antlr.v5.runtime.core.TokenStream;

public abstract class RuntimeTestParser extends Parser {
	protected java.io.PrintStream outStream = System.out;

	public RuntimeTestParser(TokenStream input) {
		super(input);
	}

	public void setOutStream(java.io.PrintStream outStream) {
		this.outStream = outStream;
	}
}
