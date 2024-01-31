package org.antlr.v5.test.runtime.states.jvm;

import org.antlr.v5.kotlinruntime.tree.ParseTree;

public class KotlinExecutedState extends JvmExecutedState<KotlinCompiledState, ParseTree> {
	public KotlinExecutedState(KotlinCompiledState previousState, String output, String errors, ParseTree parseTree,
							   Exception exception) {
		super(previousState, output, errors, parseTree, exception);
	}
}
