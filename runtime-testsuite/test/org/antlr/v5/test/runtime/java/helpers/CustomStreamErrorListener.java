package org.antlr.v5.test.runtime.java.helpers;

import org.antlr.v5.runtime.BaseErrorListener;
import org.antlr.v5.runtime.RecognitionException;
import org.antlr.v5.runtime.Recognizer;

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
