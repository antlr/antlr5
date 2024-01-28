package org.antlr.v5.test.runtime.java.helpers;

import org.antlr.v5.runtime.CharStream;
import org.antlr.v5.runtime.Lexer;

public abstract class RuntimeTestLexer extends Lexer {
	protected java.io.PrintStream outStream = System.out;

	public RuntimeTestLexer(CharStream input) { super(input); }

	public void setOutStream(java.io.PrintStream outStream) { this.outStream = outStream; }
}
