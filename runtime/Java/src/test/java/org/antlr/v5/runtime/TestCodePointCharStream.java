/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime;

import org.antlr.v5.runtime.misc.Interval;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class TestCodePointCharStream {
	@Test
	public void emptyBytesHasSize0() {
		CodePointCharStream s = CharStreams.fromString("");
		Assertions.assertEquals(0, s.size());
		Assertions.assertEquals(0, s.index());
		Assertions.assertEquals("", s.toString());
	}

	@Test
	public void emptyBytesLookAheadReturnsEOF() {
		CodePointCharStream s = CharStreams.fromString("");
		Assertions.assertEquals(IntStream.EOF, s.LA(1));
		Assertions.assertEquals(0, s.index());
	}

	@Test
	public void consumingEmptyStreamShouldThrow() {
		CodePointCharStream s = CharStreams.fromString("");
		IllegalStateException illegalStateException = Assertions.assertThrows(
				IllegalStateException.class,
				s::consume
		);
		Assertions.assertEquals("cannot consume EOF", illegalStateException.getMessage());
	}

	@Test
	public void singleLatinCodePointHasSize1() {
		CodePointCharStream s = CharStreams.fromString("X");
		Assertions.assertEquals(1, s.size());
	}

	@Test
	public void consumingSingleLatinCodePointShouldMoveIndex() {
		CodePointCharStream s = CharStreams.fromString("X");
		Assertions.assertEquals(0, s.index());
		s.consume();
		Assertions.assertEquals(1, s.index());
	}

	@Test
	public void consumingPastSingleLatinCodePointShouldThrow() {
		CodePointCharStream s = CharStreams.fromString("X");
		s.consume();
		IllegalStateException illegalStateException = Assertions.assertThrows(IllegalStateException.class, s::consume);
		Assertions.assertEquals("cannot consume EOF", illegalStateException.getMessage());
	}

	@Test
	public void singleLatinCodePointLookAheadShouldReturnCodePoint() {
		CodePointCharStream s = CharStreams.fromString("X");
		Assertions.assertEquals('X', s.LA(1));
		Assertions.assertEquals(0, s.index());
	}

	@Test
	public void multipleLatinCodePointsLookAheadShouldReturnCodePoints() {
		CodePointCharStream s = CharStreams.fromString("XYZ");
		Assertions.assertEquals('X', s.LA(1));
		Assertions.assertEquals(0, s.index());
		Assertions.assertEquals('Y', s.LA(2));
		Assertions.assertEquals(0, s.index());
		Assertions.assertEquals('Z', s.LA(3));
		Assertions.assertEquals(0, s.index());
	}

	@Test
	public void singleLatinCodePointLookAheadPastEndShouldReturnEOF() {
		CodePointCharStream s = CharStreams.fromString("X");
		Assertions.assertEquals(IntStream.EOF, s.LA(2));
	}

	@Test
	public void singleCJKCodePointHasSize1() {
		CodePointCharStream s = CharStreams.fromString("\u611B");
		Assertions.assertEquals(1, s.size());
		Assertions.assertEquals(0, s.index());
	}

	@Test
	public void consumingSingleCJKCodePointShouldMoveIndex() {
		CodePointCharStream s = CharStreams.fromString("\u611B");
		Assertions.assertEquals(0, s.index());
		s.consume();
		Assertions.assertEquals(1, s.index());
	}

	@Test
	public void consumingPastSingleCJKCodePointShouldThrow() {
		CodePointCharStream s = CharStreams.fromString("\u611B");
		s.consume();
		IllegalStateException illegalStateException = Assertions.assertThrows(IllegalStateException.class, s::consume);
		Assertions.assertEquals("cannot consume EOF", illegalStateException.getMessage());
	}

	@Test
	public void singleCJKCodePointLookAheadShouldReturnCodePoint() {
		CodePointCharStream s = CharStreams.fromString("\u611B");
		Assertions.assertEquals(0x611B, s.LA(1));
		Assertions.assertEquals(0, s.index());
	}

	@Test
	public void singleCJKCodePointLookAheadPastEndShouldReturnEOF() {
		CodePointCharStream s = CharStreams.fromString("\u611B");
		Assertions.assertEquals(IntStream.EOF, s.LA(2));
		Assertions.assertEquals(0, s.index());
	}

	@Test
	public void singleEmojiCodePointHasSize1() {
		CodePointCharStream s = CharStreams.fromString(
				new StringBuilder().appendCodePoint(0x1F4A9).toString());
		Assertions.assertEquals(1, s.size());
		Assertions.assertEquals(0, s.index());
	}

	@Test
	public void consumingSingleEmojiCodePointShouldMoveIndex() {
		CodePointCharStream s = CharStreams.fromString(
				new StringBuilder().appendCodePoint(0x1F4A9).toString());
		Assertions.assertEquals(0, s.index());
		s.consume();
		Assertions.assertEquals(1, s.index());
	}

	@Test
	public void consumingPastEndOfEmojiCodePointWithShouldThrow() {
		CodePointCharStream s = CharStreams.fromString(
				new StringBuilder().appendCodePoint(0x1F4A9).toString());
		Assertions.assertEquals(0, s.index());
		s.consume();
		Assertions.assertEquals(1, s.index());
		IllegalStateException illegalStateException = Assertions.assertThrows(IllegalStateException.class, s::consume);
		Assertions.assertEquals("cannot consume EOF", illegalStateException.getMessage());
	}

	@Test
	public void singleEmojiCodePointLookAheadShouldReturnCodePoint() {
		CodePointCharStream s = CharStreams.fromString(
				new StringBuilder().appendCodePoint(0x1F4A9).toString());
		Assertions.assertEquals(0x1F4A9, s.LA(1));
		Assertions.assertEquals(0, s.index());
	}

	@Test
	public void singleEmojiCodePointLookAheadPastEndShouldReturnEOF() {
		CodePointCharStream s = CharStreams.fromString(
				new StringBuilder().appendCodePoint(0x1F4A9).toString());
		Assertions.assertEquals(IntStream.EOF, s.LA(2));
		Assertions.assertEquals(0, s.index());
	}

	@Test
	public void getTextWithLatin() {
		CodePointCharStream s = CharStreams.fromString("0123456789");
		Assertions.assertEquals("34567", s.getText(Interval.of(3, 7)));
	}

	@Test
	public void getTextWithCJK() {
		CodePointCharStream s = CharStreams.fromString("01234\u40946789");
		Assertions.assertEquals("34\u409467", s.getText(Interval.of(3, 7)));
	}

	@Test
	public void getTextWithEmoji() {
		CodePointCharStream s = CharStreams.fromString(
				new StringBuilder("01234")
					.appendCodePoint(0x1F522)
					.append("6789")
					.toString());
		Assertions.assertEquals("34\uD83D\uDD2267", s.getText(Interval.of(3, 7)));
	}

	@Test
	public void toStringWithLatin() {
		CodePointCharStream s = CharStreams.fromString("0123456789");
		Assertions.assertEquals("0123456789", s.toString());
	}

	@Test
	public void toStringWithCJK() {
		CodePointCharStream s = CharStreams.fromString("01234\u40946789");
		Assertions.assertEquals("01234\u40946789", s.toString());
	}

	@Test
	public void toStringWithEmoji() {
		CodePointCharStream s = CharStreams.fromString(
				new StringBuilder("01234")
					.appendCodePoint(0x1F522)
					.append("6789")
					.toString());
		Assertions.assertEquals("01234\uD83D\uDD226789", s.toString());
	}

	@Test
	public void lookAheadWithLatin() {
		CodePointCharStream s = CharStreams.fromString("0123456789");
		Assertions.assertEquals('5', s.LA(6));
	}

	@Test
	public void lookAheadWithCJK() {
		CodePointCharStream s = CharStreams.fromString("01234\u40946789");
		Assertions.assertEquals(0x4094, s.LA(6));
	}

	@Test
	public void lookAheadWithEmoji() {
		CodePointCharStream s = CharStreams.fromString(
				new StringBuilder("01234")
					.appendCodePoint(0x1F522)
					.append("6789")
					.toString());
		Assertions.assertEquals(0x1F522, s.LA(6));
	}

	@Test
	public void seekWithLatin() {
		CodePointCharStream s = CharStreams.fromString("0123456789");
		s.seek(5);
		Assertions.assertEquals('5', s.LA(1));
	}

	@Test
	public void seekWithCJK() {
		CodePointCharStream s = CharStreams.fromString("01234\u40946789");
		s.seek(5);
		Assertions.assertEquals(0x4094, s.LA(1));
	}

	@Test
	public void seekWithEmoji() {
		CodePointCharStream s = CharStreams.fromString(
				new StringBuilder("01234")
					.appendCodePoint(0x1F522)
					.append("6789")
					.toString());
		s.seek(5);
		Assertions.assertEquals(0x1F522, s.LA(1));
	}

	@Test
	public void lookBehindWithLatin() {
		CodePointCharStream s = CharStreams.fromString("0123456789");
		s.seek(6);
		Assertions.assertEquals('5', s.LA(-1));
	}

	@Test
	public void lookBehindWithCJK() {
		CodePointCharStream s = CharStreams.fromString("01234\u40946789");
		s.seek(6);
		Assertions.assertEquals(0x4094, s.LA(-1));
	}

	@Test
	public void lookBehindWithEmoji() {
		CodePointCharStream s = CharStreams.fromString(
				new StringBuilder("01234")
					.appendCodePoint(0x1F522)
					.append("6789")
					.toString());
		s.seek(6);
		Assertions.assertEquals(0x1F522, s.LA(-1));
	}

	@Test
	public void asciiContentsShouldUse8BitBuffer() {
		CodePointCharStream s = CharStreams.fromString("hello");
		Assertions.assertTrue(s.getInternalStorage() instanceof byte[]);
		Assertions.assertEquals(5, s.size());
	}

	@Test
	public void bmpContentsShouldUse16BitBuffer() {
		CodePointCharStream s = CharStreams.fromString("hello \u4E16\u754C");
		Assertions.assertTrue(s.getInternalStorage() instanceof char[]);
		Assertions.assertEquals(8, s.size());
	}

	@Test
	public void smpContentsShouldUse32BitBuffer() {
		CodePointCharStream s = CharStreams.fromString("hello \uD83C\uDF0D");
		Assertions.assertTrue(s.getInternalStorage() instanceof int[]);
		Assertions.assertEquals(7, s.size());
	}
}
