/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.test.tool;

import org.antlr.v5.misc.GrammarLiteralParser;
import org.antlr.v5.misc.CharParseResult;
import org.antlr.v5.runtime.core.misc.IntervalSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestGrammarLiteralParser {
	@Test
	public void testParseCharValueFromGrammarStringLiteral() {
		assertInstanceOf(CharParseResult.Invalid.class, GrammarLiteralParser.parseCharFromStringLiteral(null));
		assertInstanceOf(CharParseResult.Invalid.class, GrammarLiteralParser.parseCharFromStringLiteral(""));
		assertInstanceOf(CharParseResult.Invalid.class, GrammarLiteralParser.parseCharFromStringLiteral("b"));
		assertEquals(111, ((CharParseResult.CodePoint)GrammarLiteralParser.parseCharFromStringLiteral("'o'")).codePoint);
	}

	@Test
	public void testParseStringFromGrammarStringLiteral() {
		assertNull(GrammarLiteralParser.parseStringFromStringLiteral("foo\\u{bbb"));
		assertNull(GrammarLiteralParser.parseStringFromStringLiteral("foo\\u{[]bb"));
		assertNull(GrammarLiteralParser.parseStringFromStringLiteral("foo\\u[]bb"));
		assertNull(GrammarLiteralParser.parseStringFromStringLiteral("foo\\ubb"));

		assertEquals("oo»b", GrammarLiteralParser.parseStringFromStringLiteral("foo\\u{bb}bb"));
	}

	@Test
	public void testParseCharFromGrammarStringLiteral() {
		assertEquals(102, parseUnquotedChar("f", true));

		assertEquals(-1, parseUnquotedChar("' ", true));
		assertEquals(-1, parseUnquotedChar("\\ ", true));
		assertEquals(39, parseUnquotedChar("\\'", true));
		assertEquals(10, parseUnquotedChar("\\n", true));
		assertEquals(-1, parseUnquotedChar("\\]", true));
		assertEquals(-1, parseUnquotedChar("\\-", true));

		assertEquals(-1, parseUnquotedChar("foobar", true));
		assertEquals(4660, parseUnquotedChar("\\u1234", true));
		assertEquals(18, parseUnquotedChar("\\u{12}", true));

		assertEquals(-1, parseUnquotedChar("\\u{", true));
		assertEquals(-1, parseUnquotedChar("foo", true));
	}

	@Test
	public void testParseCharFromGrammarSetLiteral() {
		assertEquals(93, parseUnquotedChar("\\]", false));
		assertEquals(45, parseUnquotedChar("\\-", false));
		assertEquals(-1, parseUnquotedChar("\\'", false));
	}

	@Test
	public void testParseHexValue() {
		assertEquals(-1, GrammarLiteralParser.parseHexValue("foobar", -1, 3));
		assertEquals(-1, GrammarLiteralParser.parseHexValue("foobar", 1, -1));
		assertEquals(-1, GrammarLiteralParser.parseHexValue("foobar", 1, 3));
		assertEquals(35, GrammarLiteralParser.parseHexValue("123456", 1, 3));
	}

	@Test
	public void testParseEmpty() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral(""));
	}

	@Test
	public void testParseJustBackslash() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral("\\"));
	}

	@Test
	public void testParseInvalidEscape() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral("\\z"));
	}

	@Test
	public void testParseNewline() {
		assertEquals(new CharParseResult.CodePoint('\n', 0,2), parseCharInSetLiteral("\\n"));
	}

	@Test
	public void testParseTab() {
		assertEquals(new CharParseResult.CodePoint('\t', 0,2), parseCharInSetLiteral("\\t"));
	}

	@Test
	public void testParseUnicodeTooShort() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral("\\uABC"));
	}

	@Test
	public void testParseUnicodeBMP() {
		assertEquals(new CharParseResult.CodePoint(0xABCD, 0,6), parseCharInSetLiteral("\\uABCD"));
	}

	@Test
	public void testParseUnicodeSMPTooShort() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral("\\u{}"));
	}

	@Test
	public void testParseUnicodeSMPMissingCloseBrace() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral("\\u{12345"));
	}

	@Test
	public void testParseUnicodeTooBig() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral("\\u{110000}"));
	}

	@Test
	public void testParseUnicodeSMP() {
		assertEquals(new CharParseResult.CodePoint(0x10ABCD, 0, 10), parseCharInSetLiteral("\\u{10ABCD}"));
	}

	@Test
	public void testParseUnicodePropertyTooShort() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral("\\p{}"));
	}

	@Test
	public void testParseUnicodePropertyMissingCloseBrace() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral("\\p{1234"));
	}

	@Test
	public void testParseUnicodeProperty() {
		assertEquals(
				new CharParseResult.Property(IntervalSet.Companion.of(66560, 66639), 0, 11),
				parseCharInSetLiteral("\\p{Deseret}"));
	}

	@Test
	public void testParseUnicodePropertyInvertedTooShort() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral("\\P{}"));
	}

	@Test
	public void testParseUnicodePropertyInvertedMissingCloseBrace() {
		assertInstanceOf(CharParseResult.Invalid.class, parseCharInSetLiteral("\\P{Deseret"));
	}

	@Test
	public void testParseUnicodePropertyInverted() {
		IntervalSet expected = IntervalSet.Companion.of(0, 66559);
		expected.add(66640, Character.MAX_CODE_POINT);
		assertEquals(new CharParseResult.Property(expected, 0, 11), parseCharInSetLiteral("\\P{Deseret}"));
	}

	private int parseUnquotedChar(String s, boolean isStringLiteral) {
		CharParseResult result = GrammarLiteralParser.parseChar(s, isStringLiteral, false);
		return result instanceof CharParseResult.CodePoint ? ((CharParseResult.CodePoint)result).codePoint : -1;
	}

	private CharParseResult parseCharInSetLiteral(String s) {
		return GrammarLiteralParser.parseNextChar(s, 0, s.length(), false);
	}
}
