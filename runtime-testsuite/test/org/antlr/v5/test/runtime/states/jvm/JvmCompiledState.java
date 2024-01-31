/*
 * Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.test.runtime.states.jvm;

import org.antlr.v5.test.runtime.states.CompiledState;
import org.antlr.v5.test.runtime.states.GeneratedState;

import java.lang.reflect.InvocationTargetException;

public abstract class JvmCompiledState<TLexer, TParser, TTokenStream> extends CompiledState {
	public final ClassLoader loader;
	public final Class<? extends TLexer> lexer;
	public final Class<? extends TParser> parser;

	public JvmCompiledState(GeneratedState previousState,
                            ClassLoader loader,
                            Class<? extends TLexer> lexer,
                            Class<? extends TParser> parser,
                            Exception exception
	) {
		super(previousState, exception);
		this.loader = loader;
		this.lexer = lexer;
		this.parser = parser;
	}

	public abstract TLexer initializeLexer(String input) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;


	public abstract TParser initializeParser(TTokenStream tokenStream) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;
}
