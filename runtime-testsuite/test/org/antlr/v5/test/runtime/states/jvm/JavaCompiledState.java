package org.antlr.v5.test.runtime.states.jvm;

import kotlin.Pair;
import org.antlr.v5.runtime.java.CharStreams;
import org.antlr.v5.runtime.core.CharStream;
import org.antlr.v5.runtime.core.TokenStream;
import org.antlr.v5.runtime.core.CommonTokenStream;
import org.antlr.v5.runtime.core.Lexer;
import org.antlr.v5.runtime.core.Parser;
import org.antlr.v5.test.runtime.RuntimeRunner;
import org.antlr.v5.test.runtime.states.GeneratedState;

import java.lang.reflect.InvocationTargetException;

public class JavaCompiledState extends JvmCompiledState<Lexer, Parser, CommonTokenStream> {
	public JavaCompiledState(GeneratedState previousState, ClassLoader loader, Class<? extends Lexer> lexer, Class<? extends Parser> parser, Exception exception) {
		super(previousState, loader, lexer, parser, exception);
	}

	public Pair<Lexer, Parser> initializeDummyLexerAndParser()
		throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		return initializeLexerAndParser("");
	}

	public Pair<Lexer, Parser> initializeLexerAndParser(String input)
		throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		Lexer lexer = initializeLexer(input);
		Parser parser = initializeParser(new CommonTokenStream(lexer));
		return new Pair<>(lexer, parser);
	}

	@Override
	public Lexer initializeLexer(String input) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		CharStream inputString = CharStreams.fromString(input, RuntimeRunner.InputFileName);
		return lexer.getConstructor(CharStream.class).newInstance(inputString);
	}

	@Override
	public Parser initializeParser(CommonTokenStream tokenStream) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		return parser.getConstructor(TokenStream.class).newInstance(tokenStream);
	}
}
