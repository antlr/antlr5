/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime._unused.tree.xpath;

import org.antlr.v5.runtime.core.*;
import org.antlr.v5.runtime.core.atn.LexerATNSimulator;
import org.antlr.v5.runtime.core.error.LexerNoViableAltException;
import org.antlr.v5.runtime.core.atn.ATN;
import org.antlr.v5.runtime.core.misc.Interval;
import org.jetbrains.annotations.NotNull;

/** Mimic the old XPathLexer from .g4 file */
public class XPathLexer extends Lexer {
	public static final int
		TOKEN_REF=1, RULE_REF=2, ANYWHERE=3, ROOT=4, WILDCARD=5, BANG=6, ID=7,
		STRING=8;
	public final static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"ANYWHERE", "ROOT", "WILDCARD", "BANG", "ID", "NameChar", "NameStartChar",
		"STRING"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, "'//'", "'/'", "'*'", "'!'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "TOKEN_REF", "RULE_REF", "ANYWHERE", "ROOT", "WILDCARD", "BANG",
		"ID", "STRING"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES, null);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@NotNull
	@Override
	public String getGrammarFileName() { return "XPathLexer.g4"; }

	@NotNull
	@Override
	public String[] getRuleNames() { return ruleNames; }

	@NotNull
	@Override
	public String[] getModeNames() { return modeNames; }

	@NotNull
	@Override
	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	ATN atn = null;

	@NotNull
	@Override
	public ATN getAtn() {
		return atn;
	}

	public void setAtn(ATN atn) {
		this.atn = atn;
	}

	LexerATNSimulator interpreter;
	@NotNull
	public LexerATNSimulator getInterpreter() {
		return interpreter;
	}

	@Override
	public void setInterpreter(@NotNull LexerATNSimulator simulator) {
		//noinspection DataFlowIssue
		this.interpreter = interpreter;
	}

	@Override
	public void setTokenFactory(TokenFactory<? extends Token> tokenFactory) {
		// TODO
	}

	protected int line = 1;
	protected int charPositionInLine = 0;

	public XPathLexer(CharStream input) {
		super(input);
	}

	@NotNull
	@Override
	public Token nextToken() {
		set_tokenStartCharIndex(get_input().index());
		CommonToken t = null;
		while ( t==null ) {
			switch ( get_input().LA(1) ) {
				case '/':
					consume();
					if ( get_input().LA(1)=='/' ) {
						consume();
						t = new CommonToken(ANYWHERE, "//");
					}
					else {
						t = new CommonToken(ROOT, "/");
					}
					break;
				case '*':
					consume();
					t = new CommonToken(WILDCARD, "*");
					break;
				case '!':
					consume();
					t = new CommonToken(BANG, "!");
					break;
				case '\'':
					String s = matchString();
					t = new CommonToken(STRING, s);
					break;
				case CharStream.EOF :
					return new CommonToken(EOF, "<EOF>");
				default:
					if ( isNameStartChar(get_input().LA(1)) ) {
						String id = matchID();
						if ( Character.isUpperCase(id.charAt(0)) ) t = new CommonToken(TOKEN_REF, id);
						else t = new CommonToken(RULE_REF, id);
					}
					else {
						throw new LexerNoViableAltException(this, get_input(), get_tokenStartCharIndex(), null);
					}
					break;
			}
		}
		t.setStartIndex(get_tokenStartCharIndex());
		t.setCharPositionInLine(get_tokenStartCharIndex());
		t.setLine(line);
		return t;
	}

	public void consume() {
		int curChar = get_input().LA(1);
		if ( curChar=='\n' ) {
			line++;
			charPositionInLine=0;
		}
		else {
			charPositionInLine++;
		}
		get_input().consume();
	}

	@Override
	public int getCharPositionInLine() {
		return charPositionInLine;
	}

	public String matchID() {
		int start = get_input().index();
		consume(); // drop start char
		while ( isNameChar(get_input().LA(1)) ) {
			consume();
		}
		return get_input().getText(Interval.Companion.of(start,get_input().index()-1));
	}

	public String matchString() {
		int start = get_input().index();
		consume(); // drop first quote
		while ( get_input().LA(1)!='\'' ) {
			consume();
		}
		consume(); // drop last quote
		return get_input().getText(Interval.Companion.of(start,get_input().index()-1));
	}

	public boolean isNameChar(int c) { return Character.isUnicodeIdentifierPart(c); }

	public boolean isNameStartChar(int c) { return Character.isUnicodeIdentifierStart(c); }
}
