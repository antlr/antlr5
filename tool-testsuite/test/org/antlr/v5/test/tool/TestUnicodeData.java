/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.test.tool;
import org.antlr.v5.unicode.UnicodeData;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestUnicodeData {
	@Test
	public void testUnicodeGeneralCategoriesLatin() {
		assertTrue(UnicodeData.getPropertyCodePoints("Lu").contains('X'));
		assertFalse(UnicodeData.getPropertyCodePoints("Lu").contains('x'));
		assertTrue(UnicodeData.getPropertyCodePoints("Ll").contains('x'));
		assertFalse(UnicodeData.getPropertyCodePoints("Ll").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains('x'));
		assertTrue(UnicodeData.getPropertyCodePoints("N").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Z").contains(' '));
	}

	@Test
	public void testUnicodeGeneralCategoriesBMP() {
		assertTrue(UnicodeData.getPropertyCodePoints("Lu").contains('\u1E3A'));
		assertFalse(UnicodeData.getPropertyCodePoints("Lu").contains('\u1E3B'));
		assertTrue(UnicodeData.getPropertyCodePoints("Ll").contains('\u1E3B'));
		assertFalse(UnicodeData.getPropertyCodePoints("Ll").contains('\u1E3A'));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains('\u1E3A'));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains('\u1E3B'));
		assertTrue(UnicodeData.getPropertyCodePoints("N").contains('\u1BB0'));
		assertFalse(UnicodeData.getPropertyCodePoints("N").contains('\u1E3A'));
		assertTrue(UnicodeData.getPropertyCodePoints("Z").contains('\u2028'));
		assertFalse(UnicodeData.getPropertyCodePoints("Z").contains('\u1E3A'));
	}

	@Test
	public void testUnicodeGeneralCategoriesSMP() {
		assertTrue(UnicodeData.getPropertyCodePoints("Lu").contains(0x1D5D4));
		assertFalse(UnicodeData.getPropertyCodePoints("Lu").contains(0x1D770));
		assertTrue(UnicodeData.getPropertyCodePoints("Ll").contains(0x1D770));
		assertFalse(UnicodeData.getPropertyCodePoints("Ll").contains(0x1D5D4));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains(0x1D5D4));
		assertTrue(UnicodeData.getPropertyCodePoints("L").contains(0x1D770));
		assertTrue(UnicodeData.getPropertyCodePoints("N").contains(0x11C50));
		assertFalse(UnicodeData.getPropertyCodePoints("N").contains(0x1D5D4));
	}

	@Test
	public void testUnicodeCategoryAliases() {
		assertTrue(UnicodeData.getPropertyCodePoints("Lowercase_Letter").contains('x'));
		assertFalse(UnicodeData.getPropertyCodePoints("Lowercase_Letter").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Letter").contains('x'));
		assertFalse(UnicodeData.getPropertyCodePoints("Letter").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Enclosing_Mark").contains(0x20E2));
		assertFalse(UnicodeData.getPropertyCodePoints("Enclosing_Mark").contains('x'));
	}

	@Test
	public void testUnicodeBinaryProperties() {
		assertTrue(UnicodeData.getPropertyCodePoints("Emoji").contains(0x1F4A9));
		assertFalse(UnicodeData.getPropertyCodePoints("Emoji").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("alnum").contains('9'));
		assertFalse(UnicodeData.getPropertyCodePoints("alnum").contains(0x1F4A9));
		assertTrue(UnicodeData.getPropertyCodePoints("Dash").contains('-'));
		assertTrue(UnicodeData.getPropertyCodePoints("Hex").contains('D'));
		assertFalse(UnicodeData.getPropertyCodePoints("Hex").contains('Q'));
	}

	@Test
	public void testUnicodeBinaryPropertyAliases() {
		assertTrue(UnicodeData.getPropertyCodePoints("Ideo").contains('\u611B'));
		assertFalse(UnicodeData.getPropertyCodePoints("Ideo").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Soft_Dotted").contains('\u0456'));
		assertFalse(UnicodeData.getPropertyCodePoints("Soft_Dotted").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Noncharacter_Code_Point").contains('\uFFFF'));
		assertFalse(UnicodeData.getPropertyCodePoints("Noncharacter_Code_Point").contains('X'));
	}

	@Test
	public void testUnicodeScripts() {
		assertTrue(UnicodeData.getPropertyCodePoints("Zyyy").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Latn").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Hani").contains(0x4E04));
		assertTrue(UnicodeData.getPropertyCodePoints("Cyrl").contains(0x0404));
	}

	@Test
	public void testUnicodeScriptEquals() {
		assertTrue(UnicodeData.getPropertyCodePoints("Script=Zyyy").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Script=Latn").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Script=Hani").contains(0x4E04));
		assertTrue(UnicodeData.getPropertyCodePoints("Script=Cyrl").contains(0x0404));
	}

	@Test
	public void testUnicodeScriptAliases() {
		assertTrue(UnicodeData.getPropertyCodePoints("Common").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Latin").contains('X'));
		assertTrue(UnicodeData.getPropertyCodePoints("Han").contains(0x4E04));
		assertTrue(UnicodeData.getPropertyCodePoints("Cyrillic").contains(0x0404));
	}

	@Test
	public void testUnicodeBlocks() {
		assertTrue(UnicodeData.getPropertyCodePoints("InASCII").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("InCJK").contains(0x4E04));
		assertTrue(UnicodeData.getPropertyCodePoints("InCyrillic").contains(0x0404));
		assertTrue(UnicodeData.getPropertyCodePoints("InMisc_Pictographs").contains(0x1F4A9));
	}

	@Test
	public void testUnicodeBlockEquals() {
		assertTrue(UnicodeData.getPropertyCodePoints("Block=ASCII").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Block=CJK").contains(0x4E04));
		assertTrue(UnicodeData.getPropertyCodePoints("Block=Cyrillic").contains(0x0404));
		assertTrue(UnicodeData.getPropertyCodePoints("Block=Misc_Pictographs").contains(0x1F4A9));
	}

	@Test
	public void testUnicodeBlockAliases() {
		assertTrue(UnicodeData.getPropertyCodePoints("InBasic_Latin").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("InMiscellaneous_Mathematical_Symbols_B").contains(0x29BE));
	}

	@Test
	public void testEnumeratedPropertyEquals() {
		assertFalse(
				UnicodeData.getPropertyCodePoints("Grapheme_Cluster_Break=E_Base").contains(0x1F47E),
				"U+1F47E ALIEN MONSTER is not an emoji modifier");

		assertFalse(
				UnicodeData.getPropertyCodePoints("Grapheme_Cluster_Break=E_Base").contains(0x1038),
				"U+1038 MYANMAR SIGN VISARGA is not a spacing mark");

		assertTrue(
				UnicodeData.getPropertyCodePoints("East_Asian_Width=Ambiguous").contains(0x00A1),
				"U+00A1 INVERTED EXCLAMATION MARK has ambiguous East Asian Width");

		assertFalse(
				UnicodeData.getPropertyCodePoints("East_Asian_Width=Ambiguous").contains(0x00A2),
				"U+00A2 CENT SIGN does not have ambiguous East Asian Width");
	}

        @Test
        public void extendedPictographic() {
		assertTrue(
				UnicodeData.getPropertyCodePoints("Extended_Pictographic").contains(0x1F588),
				"U+1F588 BLACK PUSHPIN is in Extended Pictographic");
		assertFalse(
				UnicodeData.getPropertyCodePoints("Extended_Pictographic").contains('0'),
				"0 is not in Extended Pictographic");
        }

        @Test
        public void emojiPresentation() {
		assertTrue(
				UnicodeData.getPropertyCodePoints("EmojiPresentation=EmojiDefault").contains(0x1F4A9),
				"U+1F4A9 PILE OF POO is in EmojiPresentation=EmojiDefault");
		assertFalse(
				UnicodeData.getPropertyCodePoints("EmojiPresentation=EmojiDefault").contains('0'),
				"0 is not in EmojiPresentation=EmojiDefault");
		assertFalse(
				UnicodeData.getPropertyCodePoints("EmojiPresentation=EmojiDefault").contains('A'),
				"A is not in EmojiPresentation=EmojiDefault");
		assertFalse(
				UnicodeData.getPropertyCodePoints("EmojiPresentation=TextDefault").contains(0x1F4A9),
				"U+1F4A9 PILE OF POO is not in EmojiPresentation=TextDefault");
		assertTrue(
				UnicodeData.getPropertyCodePoints("EmojiPresentation=TextDefault").contains('0'),
				"0 is in EmojiPresentation=TextDefault");
		assertFalse(
				UnicodeData.getPropertyCodePoints("EmojiPresentation=TextDefault").contains('A'),
				"A is not in EmojiPresentation=TextDefault");
        }

	@Test
	public void testPropertyCaseInsensitivity() {
		assertTrue(UnicodeData.getPropertyCodePoints("l").contains('x'));
		assertFalse(UnicodeData.getPropertyCodePoints("l").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("common").contains('0'));
		assertTrue(UnicodeData.getPropertyCodePoints("Alnum").contains('0'));
	}

	@Test
	public void testPropertyDashSameAsUnderscore() {
		assertTrue(UnicodeData.getPropertyCodePoints("InLatin-1").contains('\u00F0'));
	}

	@Test
	public void modifyingUnicodeDataShouldThrow() {
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> UnicodeData.getPropertyCodePoints("L").add(0x12345));
		assertEquals("can't alter readonly IntervalSet", exception.getMessage());
	}
}
