/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.java;

import org.antlr.v5.runtime.core.Recognizer;
import org.antlr.v5.runtime.core.error.RecognitionException;

/**
 *
 * @author Sam Harwell
 */
public class ConsoleErrorListener extends BaseErrorListener {
	/**
	 * Provides a default instance of {@link ConsoleErrorListener}.
	 */
	public static final ConsoleErrorListener INSTANCE = new ConsoleErrorListener();

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This implementation prints messages to {@link System#err} containing the
	 * values of {@code line}, {@code charPositionInLine}, and {@code msg} using
	 * the following format.</p>
	 *
	 * <pre>
	 * line <em>line</em>:<em>charPositionInLine</em> <em>msg</em>
	 * </pre>
	 */
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
							Object offendingSymbol,
							int line,
							int charPositionInLine,
							String msg,
							RecognitionException e)
	{
		System.err.println("line " + line + ":" + charPositionInLine + " " + msg);
	}

}
