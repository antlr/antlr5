/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.test.tool;

import org.antlr.v5.runtime.core.*;
import org.antlr.v5.runtime.java.CharStreams;
import org.antlr.v5.tool.LexerGrammar;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestBufferedTokenStream {
	protected TokenStream createTokenStream(TokenSource src) {
		return new BufferedTokenStream(src);
	}

	@Test public void testFirstToken() throws Exception {
        LexerGrammar g = new LexerGrammar(
            "lexer grammar t;\n"+
            "ID : 'a'..'z'+;\n" +
            "INT : '0'..'9'+;\n" +
            "SEMI : ';';\n" +
            "ASSIGN : '=';\n" +
            "PLUS : '+';\n" +
            "MULT : '*';\n" +
            "WS : ' '+;\n");
        // Tokens: 012345678901234567
        // Input:  x = 3 * 0 + 2 * 0;
        CharStream input = CharStreams.fromString("x = 3 * 0 + 2 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = createTokenStream(lexEngine);

        String result = tokens.LT(1).getText();
        String expecting = "x";
        assertEquals(expecting, result);
    }

    @Test public void test2ndToken() throws Exception {
        LexerGrammar g = new LexerGrammar(
            "lexer grammar t;\n"+
            "ID : 'a'..'z'+;\n" +
            "INT : '0'..'9'+;\n" +
            "SEMI : ';';\n" +
            "ASSIGN : '=';\n" +
            "PLUS : '+';\n" +
            "MULT : '*';\n" +
            "WS : ' '+;\n");
        // Tokens: 012345678901234567
        // Input:  x = 3 * 0 + 2 * 0;
        CharStream input = CharStreams.fromString("x = 3 * 0 + 2 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = createTokenStream(lexEngine);

        String result = tokens.LT(2).getText();
        String expecting = " ";
        assertEquals(expecting, result);
    }

    @Test public void testCompleteBuffer() throws Exception {
        LexerGrammar g = new LexerGrammar(
            "lexer grammar t;\n"+
            "ID : 'a'..'z'+;\n" +
            "INT : '0'..'9'+;\n" +
            "SEMI : ';';\n" +
            "ASSIGN : '=';\n" +
            "PLUS : '+';\n" +
            "MULT : '*';\n" +
            "WS : ' '+;\n");
        // Tokens: 012345678901234567
        // Input:  x = 3 * 0 + 2 * 0;
        CharStream input = CharStreams.fromString("x = 3 * 0 + 2 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = createTokenStream(lexEngine);

        int i = 1;
        Token t = tokens.LT(i);
        while ( t.getType()!=Token.EOF ) {
            i++;
            t = tokens.LT(i);
        }
        tokens.LT(i++); // push it past end
        tokens.LT(i++);

        String result = tokens.getText();
        String expecting = "x = 3 * 0 + 2 * 0;";
        assertEquals(expecting, result);
    }

    @Test public void testCompleteBufferAfterConsuming() throws Exception {
        LexerGrammar g = new LexerGrammar(
            "lexer grammar t;\n"+
            "ID : 'a'..'z'+;\n" +
            "INT : '0'..'9'+;\n" +
            "SEMI : ';';\n" +
            "ASSIGN : '=';\n" +
            "PLUS : '+';\n" +
            "MULT : '*';\n" +
            "WS : ' '+;\n");
        // Tokens: 012345678901234567
        // Input:  x = 3 * 0 + 2 * 0;
        CharStream input = CharStreams.fromString("x = 3 * 0 + 2 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = createTokenStream(lexEngine);

        Token t = tokens.LT(1);
        while ( t.getType()!=Token.EOF ) {
            tokens.consume();
            t = tokens.LT(1);
        }

        String result = tokens.getText();
        String expecting = "x = 3 * 0 + 2 * 0;";
        assertEquals(expecting, result);
    }

    @Test public void testLookback() throws Exception {
        LexerGrammar g = new LexerGrammar(
            "lexer grammar t;\n"+
            "ID : 'a'..'z'+;\n" +
            "INT : '0'..'9'+;\n" +
            "SEMI : ';';\n" +
            "ASSIGN : '=';\n" +
            "PLUS : '+';\n" +
            "MULT : '*';\n" +
            "WS : ' '+;\n");
        // Tokens: 012345678901234567
        // Input:  x = 3 * 0 + 2 * 0;
        CharStream input = CharStreams.fromString("x = 3 * 0 + 2 * 0;");
        LexerInterpreter lexEngine = g.createLexerInterpreter(input);
        TokenStream tokens = createTokenStream(lexEngine);

        tokens.consume(); // get x into buffer
        Token t = tokens.LT(-1);
        assertEquals("x", t.getText());

        tokens.consume();
        tokens.consume(); // consume '='
        t = tokens.LT(-3);
        assertEquals("x", t.getText());
        t = tokens.LT(-2);
        assertEquals(" ", t.getText());
        t = tokens.LT(-1);
        assertEquals("=", t.getText());
    }

}
