/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.test.runtime.java.api;

import org.antlr.v5.runtime.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class contains tests for specific API functionality in {@link TokenStream} and derived types.
 */
public class TestTokenStream {

	/**
	 * This is a targeted regression test for antlr/antlr4#1584 ({@link BufferedTokenStream} cannot be reused after EOF).
	 */
	@Test
	public void testBufferedTokenStreamReuseAfterFill() {
		CharStream firstInput = CharStreams.fromString("A");
		BufferedTokenStream tokenStream = new BufferedTokenStream(new VisitorBasicLexer(firstInput));
		tokenStream.fill();
		assertEquals(2, tokenStream.size());
		assertEquals(VisitorBasicLexer.A, tokenStream.get(0).getType());
		assertEquals(Token.EOF, tokenStream.get(1).getType());

		CharStream secondInput = CharStreams.fromString("AA");
		tokenStream.setTokenSource(new VisitorBasicLexer(secondInput));
		tokenStream.fill();
		assertEquals(3, tokenStream.size());
		assertEquals(VisitorBasicLexer.A, tokenStream.get(0).getType());
		assertEquals(VisitorBasicLexer.A, tokenStream.get(1).getType());
		assertEquals(Token.EOF, tokenStream.get(2).getType());
	}
}
