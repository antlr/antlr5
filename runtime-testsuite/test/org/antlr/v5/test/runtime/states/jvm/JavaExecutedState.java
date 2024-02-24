package org.antlr.v5.test.runtime.states.jvm;

import org.antlr.v5.runtime.core.tree.ParseTree;

public class JavaExecutedState extends JvmExecutedState<JavaCompiledState, ParseTree> {
	public JavaExecutedState(JavaCompiledState previousState, String output, String errors, ParseTree parseTree, Exception exception) {
		super(previousState, output, errors, parseTree, exception);
	}
}
