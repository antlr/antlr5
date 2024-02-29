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
public class Sync extends SrcOp {
	public int decision;
//	public BitSetDecl expecting;
	public Sync(OutputModelFactory factory,
				GrammarAST blkOrEbnfRootAST,
				IntervalSet expecting,
				int decision,
				String position)
	{
		super(factory, blkOrEbnfRootAST);
		this.decision = decision;
//		this.expecting = factory.createExpectingBitSet(ast, decision, expecting, position);
//		factory.defineBitSet(this.expecting);
	}
}
