/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.test.tool;

import org.antlr.v5.Tool;
import org.antlr.v5.automata.LexerATNFactory;
import org.antlr.v5.automata.ParserATNFactory;
import org.antlr.v5.runtime.java.CharStreams;
import org.antlr.v5.runtime.core.CharStream;
import org.antlr.v5.runtime.core.Lexer;
import org.antlr.v5.runtime.core.Token;
import org.antlr.v5.runtime.core.atn.ATN;
import org.antlr.v5.runtime.core.atn.ATNDeserializer;
import org.antlr.v5.runtime.core.atn.ATNSerializer;
import org.antlr.v5.runtime.core.atn.LexerATNSimulator;
import org.antlr.v5.runtime.core.misc.IntegerList;
import org.antlr.v5.semantics.SemanticPipeline;
import org.antlr.v5.test.runtime.*;
import org.antlr.v5.test.runtime.PredictionMode;
import org.antlr.v5.test.runtime.java.JavaRunner;
import org.antlr.v5.test.runtime.states.ExecutedState;
import org.antlr.v5.test.runtime.states.GeneratedState;
import org.antlr.v5.test.runtime.states.State;
import org.antlr.v5.tool.ANTLRMessage;
import org.antlr.v5.tool.Grammar;
import org.antlr.v5.tool.LexerGrammar;
import org.stringtemplate.v4.ST;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ToolTestUtils {
	public static ExecutedState execLexer(String grammarStr, String input) {
		return execLexer(grammarStr, input, null, false);
	}

	public static ExecutedState execLexer(String grammarStr, String input, Path tempDir, boolean saveTestDir) {
		return execRecognizer(grammarStr, null, input, false, tempDir, saveTestDir, false);
	}

	public static ExecutedState execParser(String grammarStr, String startRuleName, String input, boolean showDiagnosticErrors) {
		return execParser(grammarStr, startRuleName, input, showDiagnosticErrors, null, false);
	}

	public static ExecutedState execParser(String grammarStr, String startRuleName,
									String input, boolean showDiagnosticErrors, Path workingDir, boolean profile
	) {
		return execRecognizer(grammarStr, startRuleName, input, showDiagnosticErrors, workingDir, false, profile);
	}

	private static ExecutedState execRecognizer(String grammarStr, String startRuleName,
										 String input, boolean showDiagnosticErrors,
										 Path workingDir, boolean saveTestDir, boolean profile) {
		RunOptions runOptions = createExecOptionsForJavaToolTests(grammarStr,
				false, true, startRuleName, input,
				profile, showDiagnosticErrors);
		try (JavaRunner runner = new JavaRunner(workingDir, saveTestDir)) {
			State result = runner.run(runOptions);
			if (!(result instanceof ExecutedState)) {
				fail(result.getErrorMessage());
			}
			return  (ExecutedState) result;
		}
	}

	public static RunOptions createExecOptionsForJavaToolTests(
			String grammarStr,
			boolean useListener, boolean useVisitor, String startRuleName,
			String input, boolean profile, boolean showDiagnosticErrors
	) {
		return new RunOptions(new String[] {grammarStr}, null, useListener, useVisitor, startRuleName,
				input, profile, showDiagnosticErrors, false, false, Stage.Execute,
				null, PredictionMode.LL, true, null);
	}

	public static void testErrors(String[] pairs, boolean printTree) {
		for (int i = 0; i < pairs.length; i += 2) {
			String grammarStr = pairs[i];
			String expect = pairs[i + 1];

			GeneratedState state = generate(grammarStr, null);
			ErrorQueue errorQueue = state.errorQueue;

			StringBuilder buf = new StringBuilder();
			for (ANTLRMessage message : errorQueue.all) {
				String newFileName = message.fileName != null ? new File(message.fileName).getName() : null;
				ANTLRMessage newMessage = new ANTLRMessage(message.getErrorType(), message.getCause(),
						newFileName, message.offendingToken, message.getArgs());
				ST st = errorQueue.tool.errMgr.getMessageTemplate(newMessage);
				buf.append(st.render());
				buf.append("\n");
			}
			String actual = buf.toString();
			String msg = grammarStr;
			msg = msg.replace("\n", "\\n");
			msg = msg.replace("\r", "\\r");
			msg = msg.replace("\t", "\\t");

			assertEquals(expect, actual, "error in: " + msg);
		}
	}

	public static GeneratedState generate(String grammar, String slaveGrammar) {
		return generate(grammar, slaveGrammar != null ? new String[] {slaveGrammar} : null, null, null, false);
	}

	public static GeneratedState generate(String grammar, String[] slaveGrammars, Path workingDir, String[] extraOptions, boolean saveTestDir) {
		RunOptions runOptions = RunOptions.createGenerationOptions(new String[] {grammar},
				slaveGrammars, false, false, null, extraOptions);

		try (Runner runner = new Runner(workingDir, saveTestDir)) {
			return Generator.generate(runOptions, null, runner.getTempDirPath(), extraOptions);
		}
	}

	public static List<String> realElements(List<String> elements) {
		return elements.subList(Token.MIN_USER_TOKEN_TYPE, elements.size());
	}

	public static String load(String fileName)
			throws IOException {
		if ( fileName==null ) {
			return null;
		}

		String fullFileName = ToolTestUtils.class.getPackage().getName().replace('.', '/')+'/'+fileName;
		int size = 65000;
		InputStream fis = ToolTestUtils.class.getClassLoader().getResourceAsStream(fullFileName);
		try (InputStreamReader isr = new InputStreamReader(fis)) {
			char[] data = new char[size];
			int n = isr.read(data);
			return new String(data, 0, n);
		}
	}

	public static ATN createATN(Grammar g, boolean useSerializer) {
		if ( g.atn==null ) {
			semanticProcess(g);
			assertEquals(0, g.tool.getNumErrors());

			ParserATNFactory f = g.isLexer() ? new LexerATNFactory((LexerGrammar) g) : new ParserATNFactory(g);

			g.atn = f.createATN();
			assertEquals(0, g.tool.getNumErrors());
		}

		ATN atn = g.atn;
		if ( useSerializer ) {
			// sets some flags in ATN
			IntegerList serialized = ATNSerializer.Companion.getSerialized(atn);
			return new ATNDeserializer().deserialize(serialized.toArray());
		}

		return atn;
	}

	public static void semanticProcess(Grammar g) {
		if ( g.ast!=null && !g.ast.hasErrors ) {
//			System.out.println(g.ast.toStringTree());
			Tool antlr = new Tool();
			SemanticPipeline sem = new SemanticPipeline(g);
			sem.process();
			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
				for (Grammar imp : g.getImportedGrammars()) {
					antlr.processNonCombinedGrammar(imp, false);
				}
			}
		}
	}

	public static IntegerList getTokenTypesViaATN(String input, LexerATNSimulator lexerATN) {
		CharStream in = CharStreams.fromString(input);
		IntegerList tokenTypes = new IntegerList();
		int ttype;
		do {
			ttype = lexerATN.match(in, Lexer.DEFAULT_MODE);
			tokenTypes.add(ttype);
		} while ( ttype!= Token.EOF );
		return tokenTypes;
	}
}
