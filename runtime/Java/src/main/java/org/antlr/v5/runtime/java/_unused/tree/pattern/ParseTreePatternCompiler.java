package org.antlr.v5.runtime.java._unused.tree.pattern;

import org.antlr.v5.runtime.core.Lexer;
import org.antlr.v5.runtime.core.Parser;
import org.antlr.v5.runtime.core.TokenSource;

public abstract class ParseTreePatternCompiler {

	public static ParseTreePattern compile(Parser parser, String pattern, int patternRuleIndex) {
		if(parser.getTokenStream() != null) {
			TokenSource tokenSource = parser.getTokenStream().getTokenSource();
			if ( tokenSource instanceof Lexer ) {
				Lexer lexer = (Lexer) tokenSource;
				return compile(parser, lexer, pattern, patternRuleIndex);
			}
		}
		throw new UnsupportedOperationException("Parser can't discover a lexer to use");
	}

	public static ParseTreePattern compile(Parser parser, Lexer lexer, String pattern, int patternRuleIndex) {
		ParseTreePatternMatcher m = new ParseTreePatternMatcher(lexer, parser);
		return m.compile(pattern, patternRuleIndex);
	}
}
