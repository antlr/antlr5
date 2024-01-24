package org.antlr.v4.test.runtime.states.jvm;

import org.antlr.v4.runtime.tree.ParseTree;

public class JavaExecutedState extends JvmExecutedState<JavaCompiledState, ParseTree> {
	public JavaExecutedState(JavaCompiledState previousState, String output, String errors, ParseTree parseTree, Exception exception) {
		super(previousState, output, errors, parseTree, exception);
	}
}
