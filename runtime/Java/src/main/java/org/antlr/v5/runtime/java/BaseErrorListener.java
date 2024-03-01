/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.java;

import org.antlr.v5.runtime.core.atn.ATNConfigSet;
import org.antlr.v5.runtime.core.Parser;
import org.antlr.v5.runtime.core.Recognizer;
import org.antlr.v5.runtime.core.error.ANTLRErrorListener;
import org.antlr.v5.runtime.core.error.RecognitionException;
import org.antlr.v5.runtime.core.dfa.DFA;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;

/**
 * Provides an empty default implementation of {@link ANTLRErrorListener}. The
 * default implementation of each method does nothing, but can be overridden as
 * necessary.
 *
 * @author Sam Harwell
 */
public class BaseErrorListener implements ANTLRErrorListener {
	@Override
	public void syntaxError(@NotNull Recognizer<?, ?> recognizer,
							Object offendingSymbol,
							int line,
							int charPositionInLine,
							String msg,
							RecognitionException e)
	{
	}

	@Override
	public void reportAmbiguity(Parser recognizer,
								DFA dfa,
								int startIndex,
								int stopIndex,
								boolean exact,
								BitSet ambigAlts,
								ATNConfigSet configs)
	{
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer,
											DFA dfa,
											int startIndex,
											int stopIndex,
											BitSet conflictingAlts,
											ATNConfigSet configs)
	{
	}

	@Override
	public void reportContextSensitivity(Parser recognizer,
										 DFA dfa,
										 int startIndex,
										 int stopIndex,
										 int prediction,
										 ATNConfigSet configs)
	{
	}
}
