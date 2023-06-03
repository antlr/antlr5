package org.antlr.v5.test.runtime;

public class GeneratedFile {
	public final String name;
	public final Type type;

	public enum Type {
		Lexer,
		Parser,
		Other
	}

	public GeneratedFile(String name, Type type) {
		this.name = name;
		this.type = type;
	}

	@Override
	public String toString() {
		return name + "; FileType:" + type;
	}
}
