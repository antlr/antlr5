/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.test.tool;

import org.antlr.v5.runtime.core.Token;
import org.antlr.v5.runtime.core.Vocabulary;
import org.antlr.v5.runtime.core.VocabularyImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Sam Harwell
 */
public class TestVocabulary {
	@Test
	public void testEmptyVocabulary() {
		assertNotNull(VocabularyImpl.Companion.getEMPTY_VOCABULARY());
		assertEquals("EOF", VocabularyImpl.Companion.getEMPTY_VOCABULARY().getSymbolicName(Token.EOF));
		assertEquals("0", VocabularyImpl.Companion.getEMPTY_VOCABULARY().getDisplayName(Token.INVALID_TYPE));
	}

	@Test
	public void testVocabularyFromTokenNames() {
		String[] tokenNames = {
			"<INVALID>",
			"TOKEN_REF", "RULE_REF", "'//'", "'/'", "'*'", "'!'", "ID", "STRING"
		};

		Vocabulary vocabulary = VocabularyImpl.Companion.fromTokenNames(tokenNames);
		assertNotNull(vocabulary);
		assertEquals("EOF", vocabulary.getSymbolicName(Token.EOF));
		for (int i = 0; i < tokenNames.length; i++) {
			assertEquals(tokenNames[i], vocabulary.getDisplayName(i));

			if (tokenNames[i].startsWith("'")) {
				assertEquals(tokenNames[i], vocabulary.getLiteralName(i));
				assertNull(vocabulary.getSymbolicName(i));
			}
			else if (Character.isUpperCase(tokenNames[i].charAt(0))) {
				assertNull(vocabulary.getLiteralName(i));
				assertEquals(tokenNames[i], vocabulary.getSymbolicName(i));
			}
			else {
				assertNull(vocabulary.getLiteralName(i));
				assertNull(vocabulary.getSymbolicName(i));
			}
		}
	}
}
