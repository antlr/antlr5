package org.antlr.v5.test.runtime;

public class GrammarFile {
	public final String grammarName;
	public final Type type;
	public final String content;

	public enum Type {
		Lexer,
		Parser,
		Combined,
	}

	public boolean containsLexer() {
		return type == Type.Lexer || type == Type.Combined;
	}

	public boolean containsParser() {
		return type == Type.Parser || type == Type.Combined;
	}

	public GrammarFile(String grammarName, Type type, String content) {
		this.grammarName = grammarName;
		this.content = content;
		this.type = type;
	}
}
