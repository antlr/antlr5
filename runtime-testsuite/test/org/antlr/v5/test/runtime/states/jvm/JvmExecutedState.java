/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.test.runtime.states.jvm;

import org.antlr.v5.test.runtime.states.CompiledState;
import org.antlr.v5.test.runtime.states.ExecutedState;

public class JvmExecutedState<TState extends CompiledState, TTree> extends ExecutedState {
	public final TTree parseTree;

	public JvmExecutedState(TState previousState, String output, String errors, TTree parseTree,
							Exception exception) {
		super(previousState, output, errors, exception);
		this.parseTree = parseTree;
	}
}
