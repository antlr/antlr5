/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.tool;

import org.antlr.runtime.Token;

/** A problem with the symbols and/or meaning of a grammar such as rule
 *  redefinition. Any msg where we can point to a location in the grammar.
 */
public class GrammarSemanticsMessage extends ANTLRMessage {
    public GrammarSemanticsMessage(ErrorType etype,
                                   String fileName,
                                   Token offendingToken,
                                   Object... args)
    {
        super(etype, fileName, offendingToken, args);
    }
}

