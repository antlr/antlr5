/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.runtime.core.misc.IntervalSet;
import org.antlr.v5.tool.ast.GrammarAST;

/** */
public class ThrowNoViableAlt extends ThrowRecognitionException {
	public ThrowNoViableAlt(OutputModelFactory factory, GrammarAST blkOrEbnfRootAST,
							IntervalSet expecting)
	{
		super(factory, blkOrEbnfRootAST, expecting);
	}
}
