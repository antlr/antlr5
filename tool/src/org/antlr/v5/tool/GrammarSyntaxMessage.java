/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.tool;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;

/** A problem with the syntax of your antlr grammar such as
 *  "The '{' came as a complete surprise to me at this point in your program"
 */
public class GrammarSyntaxMessage extends ANTLRMessage {
	public GrammarSyntaxMessage(ErrorType etype,
								String fileName,
								Token offendingToken,
								RecognitionException antlrException,
								Object... args)
	{
		super(etype, antlrException, fileName, offendingToken, args);
	}

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Override
    public RecognitionException getCause() {
        return (RecognitionException)super.getCause();
    }
}
