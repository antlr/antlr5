/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.misc;

import org.antlr.v5.runtime.misc.CharSupport;
import org.antlr.v5.runtime.misc.IntervalSet;
import org.antlr.v5.unicode.UnicodeData;

/**
 * Utility class to parse escapes like:
 *   A
 *   \\n
 *   \\uABCD
 *   \\u{10ABCD}
 *   \\p{Foo}
 *   \\P{Bar}
 *   \\p{Baz=Blech}
 *   \\P{Baz=Blech}
 */
public final class GrammarLiteralParser {
	public static String parseStringFromStringLiteral(String s) {
		StringBuilder buf = new StringBuilder(s.length());
		int index = 1;
		int endIndex = s.length() - 1;
		while (index < endIndex) {
			CharParseResult escapeParseResult = parseNextChar(s, index, endIndex, true);
			if (!(escapeParseResult instanceof CharParseResult.CodePoint)) {
				return null;
			}
			buf.appendCodePoint(((CharParseResult.CodePoint)escapeParseResult).codePoint);
			index += escapeParseResult.length;
		}
		return buf.toString();
	}

	public static CharParseResult parseCharFromStringLiteral(String s) {
		return parseChar(s, true, true);
	}

	/** Given a literal like (the 3 char sequence with single quotes) 'a',
	 *  return the int value of 'a'. Convert escape sequences here also.
	 *  Return INVALID if not single char.
	 */
	public static CharParseResult parseChar(String s, boolean isStringLiteral, boolean isQuoted) {
		if (s == null) {
			return new CharParseResult.Invalid(0, 0);
		}

		int startIndex, endIndex;
		if (isQuoted) {
			startIndex = 1;
			endIndex = s.length() - 1;
		} else {
			startIndex = 0;
			endIndex = s.length();
		}
		CharParseResult result = parseNextChar(s, startIndex, endIndex, isStringLiteral);
		if (result instanceof CharParseResult.Invalid) {
			return result;
		}
		// Disallow multiple chars
		if (result.startIndex + result.length != endIndex) {
			return new CharParseResult.Invalid(result.startIndex, result.startIndex + result.length);
		}
		return result;
	}

	/**
	 * Parses a single char or escape sequence (x or \\t or \\u1234) starting at {@code startIndex}.
	 * Returns a type of INVALID if no valid char or escape sequence were found, a Result otherwise.
	 */
	public static CharParseResult parseNextChar(String s, int startIndex, int endIndex, boolean isStringLiteral) {
		int offset = startIndex;
		if (offset + 1 > endIndex) {
			return new CharParseResult.Invalid(startIndex, endIndex);
		}

		int firstCodePoint = s.codePointAt(offset);
		if (firstCodePoint != '\\') {
			offset += Character.charCount(firstCodePoint);
			return new CharParseResult.CodePoint(firstCodePoint, startIndex, offset);
		}

		// Move past backslash
		offset++;
		if (offset + 1 > endIndex) {
			return new CharParseResult.Invalid(startIndex, endIndex);
		}
		int escaped = s.codePointAt(offset);
		// Move past escaped code point
		offset += Character.charCount(escaped);
		if (escaped == 'u') {
			// \\u{1} is the shortest we support
			if (offset + 3 > endIndex) {
				return new CharParseResult.Invalid(startIndex, endIndex);
			}
			int hexStartOffset;
			int hexEndOffset; // appears to be exclusive
			if (s.codePointAt(offset) == '{') {
				hexStartOffset = offset + 1;
				hexEndOffset = s.indexOf('}', hexStartOffset);
				if (hexEndOffset == -1 || hexEndOffset >= endIndex) {
					return new CharParseResult.Invalid(startIndex, endIndex);
				}
				offset = hexEndOffset + 1;
			}
			else {
				if (offset + 4 > endIndex) {
					return new CharParseResult.Invalid(startIndex, endIndex);
				}
				hexStartOffset = offset;
				hexEndOffset = offset + 4;
				offset = hexEndOffset;
			}
			int codePointValue = parseHexValue(s, hexStartOffset, hexEndOffset);
			if (codePointValue == -1 || codePointValue > Character.MAX_CODE_POINT) {
				return new CharParseResult.Invalid(startIndex, Math.min(startIndex + 6, endIndex));
			}
			return new CharParseResult.CodePoint(codePointValue, startIndex, offset);
		}
		else if (!isStringLiteral && (escaped == 'p' || escaped == 'P')) { // properties are only allowed in char sets
			// \p{L} is the shortest we support
			if (offset + 3 > endIndex) {
				return new CharParseResult.Invalid(startIndex, endIndex);
			}
			if (s.codePointAt(offset) != '{') {
				return new CharParseResult.Invalid(startIndex, offset);
			}
			int openBraceOffset = offset;
			int closeBraceOffset = s.indexOf('}', openBraceOffset);
			if (closeBraceOffset == -1 || closeBraceOffset >= endIndex) {
				return new CharParseResult.Invalid(startIndex, endIndex);
			}
			String propertyName = s.substring(openBraceOffset + 1, closeBraceOffset);
			IntervalSet propertyIntervalSet = UnicodeData.getPropertyCodePoints(propertyName);
			offset = closeBraceOffset + 1;
			if (propertyIntervalSet == null || propertyIntervalSet.isNil()) {
				return new CharParseResult.Invalid(startIndex, offset);
			}
			if (escaped == 'P') {
				propertyIntervalSet = propertyIntervalSet.complement(IntervalSet.COMPLETE_CHAR_SET);
			}
			return new CharParseResult.Property(propertyIntervalSet, startIndex, offset);
		}
		else {
			Character codePoint = CharSupport.EscapedCharValue.get((char) escaped);
			if (codePoint == null) {
				boolean isEscapedChar;
				if (isStringLiteral) {
					isEscapedChar = escaped == '\'';
				}
				else {
					isEscapedChar = escaped == ']' || escaped == '-';
				}

				if (isEscapedChar) {
					codePoint = (char) escaped;
				}
				else {
					return new CharParseResult.Invalid(startIndex, offset);
				}
			}
			return new CharParseResult.CodePoint(codePoint, startIndex, offset);
		}
	}

	public static int parseHexValue(String s, int startIndex, int endIndex) {
		try {
			return Integer.parseInt(s, startIndex, endIndex, 16);
		}
		catch (Exception e) {
			return -1;
		}
	}
}
