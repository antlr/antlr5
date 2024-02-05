package org.antlr.v5.test.runtime.states.jvm;

import org.antlr.v5.runtime.kotlin.*;
import org.antlr.v5.runtime.kotlin.jvm.CharStreams;
import org.antlr.v5.test.runtime.RuntimeRunner;
import org.antlr.v5.test.runtime.states.GeneratedState;

import java.lang.reflect.InvocationTargetException;

public class KotlinCompiledState extends JvmCompiledState<Lexer, Parser, CommonTokenStream> {
	public KotlinCompiledState(GeneratedState previousState, ClassLoader loader, Class<? extends Lexer> lexer,
							   Class<? extends Parser> parser, Exception exception
	) {
		super(previousState, loader, lexer, parser, exception);
	}

	@Override
	public Lexer initializeLexer(String input)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		CharStream inputStream = CharStreams.INSTANCE.fromString(input, RuntimeRunner.InputFileName);
		return lexer.getConstructor(CharStream.class).newInstance(inputStream);
	}

	@Override
	public Parser initializeParser(CommonTokenStream tokenStream)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		return parser.getConstructor(TokenStream.class).newInstance(tokenStream);
	}
}
