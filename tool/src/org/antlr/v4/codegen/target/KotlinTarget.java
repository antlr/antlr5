package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;

import java.util.*;

public class KotlinTarget extends Target {
	protected static final HashSet<String> reservedWords = new HashSet<>(Arrays.asList(
		"as",
		"break",
		"class",
		"continue",
		"do",
		"else",
		"false",
		"for",
		"fun",
		"if",
		"in",
		"interface",
		"is",
		"null",
		"object",
		"package",
		"return",
		"super",
		"this",
		"throw",
		"true",
		"try",
		"typealias",
		"typeof",
		"val",
		"var",
		"when",
		"while",

		"rule", "parserRule"
	));

	protected static final Map<Character, String> targetCharValueEscape;
	static {
		HashMap<Character, String> map = new HashMap<>();
		addEscapedChar(map, '\t', 't');
		addEscapedChar(map, '\b', 'b');
		addEscapedChar(map, '\n', 'n');
		addEscapedChar(map, '\r', 'r');
		addEscapedChar(map, '\'');
		addEscapedChar(map, '\"');
		addEscapedChar(map, '\\');
		addEscapedChar(map, '$');
		targetCharValueEscape = map;
	}

	public KotlinTarget(CodeGenerator gen) {
		super(gen);
	}

	@Override
	protected Set<String> getReservedWords() {
		return reservedWords;
	}

	@Override
	public int getSerializedATNSegmentLimit() {
		// 65535 is the class file format byte limit for a UTF-8 encoded string literal
		// 3 is the maximum number of bytes it takes to encode a value in the range 0-0xFFFF
		return 65535 / 3;
	}

	@Override
	public boolean isATNSerializedAsInts() {
		return false;
	}

	@Override
	public Map<Character, String> getTargetCharValueEscape() {
		return targetCharValueEscape;
	}

	@Override
	public String getTargetStringLiteralFromANTLRStringLiteral(CodeGenerator generator, String literal,
															   boolean addQuotes, boolean escapeSpecial
	) {
		String str = super.getTargetStringLiteralFromANTLRStringLiteral(generator, literal, addQuotes, escapeSpecial);
		return str.replace("$", "\\$");
	}
}
