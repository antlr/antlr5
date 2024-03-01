package org.antlr.v5.test.runtime.java.helpers;

import org.antlr.v5.runtime.java.BaseErrorListener;
import org.antlr.v5.runtime.core.error.RecognitionException;
import org.antlr.v5.runtime.core.Recognizer;

import java.io.PrintStream;

public class CustomStreamErrorListener extends BaseErrorListener {
	private final PrintStream printStream;

	public CustomStreamErrorListener(PrintStream printStream){
		this.printStream = printStream;
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
							Object offendingSymbol,
							int line,
							int charPositionInLine,
							String msg,
							RecognitionException e) {
		printStream.println("line " + line + ":" + charPositionInLine + " " + msg);
	}
}
