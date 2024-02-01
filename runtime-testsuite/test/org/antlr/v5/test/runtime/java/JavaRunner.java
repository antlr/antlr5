/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.test.runtime.java;

import org.antlr.v5.runtime.*;
import org.antlr.v5.runtime.atn.PredictionMode;
import org.antlr.v5.runtime.atn.ParserATNSimulator;
import org.antlr.v5.runtime.atn.ProfilingATNSimulator;
import org.antlr.v5.runtime.tree.ParseTree;
import org.antlr.v5.runtime.tree.ParseTreeWalker;
import org.antlr.v5.test.runtime.*;
import org.antlr.v5.test.runtime.java.helpers.CustomStreamErrorListener;
import org.antlr.v5.test.runtime.java.helpers.RuntimeTestLexer;
import org.antlr.v5.test.runtime.java.helpers.RuntimeTestParser;
import org.antlr.v5.test.runtime.java.helpers.TreeShapeListener;
import org.antlr.v5.test.runtime.states.*;
import org.antlr.v5.test.runtime.states.jvm.JavaCompiledState;
import org.antlr.v5.test.runtime.states.jvm.JavaExecutedState;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.antlr.v5.test.runtime.JvmRunner.InMemoryStreamHelper.initialize;

public class JavaRunner extends JvmRunner<Lexer, Parser> {
	private static JavaCompiler compiler;

	private final static DiagnosticErrorListener DiagnosticErrorListenerInstance = new DiagnosticErrorListener();

	@Override
	public String getLanguage() { return "Java"; }

	@Override
	protected String getCompilerName() { return "javac"; }

	@Override
	protected String getRecognizerSuperTypeStartMarker() { return "extends "; }

	@Override
	protected String getRecognizerSuperTypeEndMarker() { return " {"; }

	@Override
	protected void initRuntime(RunOptions runOptions) { compiler = ToolProvider.getSystemJavaCompiler(); }

	@Override
	protected void compileClassFiles(RunOptions runOptions) {
		String tempTestDir = getTempDirPath();

		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		List<File> files = new ArrayList<>();
		if (runOptions.lexerName != null) {
			files.add(new File(tempTestDir, runOptions.lexerName + "." + getExtension()));
		}
		if (runOptions.parserName != null) {
			files.add(new File(tempTestDir, runOptions.parserName + "." + getExtension()));
		}

		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(files);

		Iterable<String> compileOptions =
			Arrays.asList("-g", "-source", "17", "-target", "17", "-implicit:class", "-Xlint:-options", "-d",
				tempTestDir, "-cp", getFullClassPath());

		JavaCompiler.CompilationTask task =
			compiler.getTask(null, fileManager, null, compileOptions, null, compilationUnits);
		task.call();
	}

	@Override
	protected CompiledState createCompiledState(GeneratedState generatedState, ClassLoader loader,
												Class<? extends Lexer> lexer, Class<? extends Parser> parser,
												Exception exception) {
		return new JavaCompiledState(generatedState, loader, lexer, parser, exception);
	}

	@Override
	protected ExecutedState execute(RunOptions runOptions, CompiledState compiledState) {
		JavaCompiledState javaCompiledState = (JavaCompiledState) compiledState;
		String output = null;
		String errors = null;
		ParseTree parseTree = null;
		Exception exception = null;

		try {
			InMemoryStreamHelper outputStreamHelper = initialize();
			InMemoryStreamHelper errorsStreamHelper = initialize();

			PrintStream outStream = new PrintStream(outputStreamHelper.pipedOutputStream);
			CustomStreamErrorListener errorListener = new CustomStreamErrorListener(new PrintStream(errorsStreamHelper.pipedOutputStream));

			CommonTokenStream tokenStream;
			RuntimeTestLexer lexer;
			if (runOptions.lexerName != null) {
				lexer = (RuntimeTestLexer) javaCompiledState.initializeLexer(runOptions.input);
				lexer.setOutStream(outStream);
				lexer.removeErrorListeners();
				lexer.addErrorListener(errorListener);
				tokenStream = new CommonTokenStream(lexer);
			} else {
				lexer = null;
				tokenStream = null;
			}

			if (runOptions.parserName != null) {
				RuntimeTestParser parser = (RuntimeTestParser) javaCompiledState.initializeParser(tokenStream);
				parser.setOutStream(outStream);
				parser.removeErrorListeners();
				parser.addErrorListener(errorListener);

				if (runOptions.showDiagnosticErrors) {
					parser.addErrorListener(DiagnosticErrorListenerInstance);
				}

				if (runOptions.traceATN) {
					// Setting trace_atn_sim isn't thread-safe,
					// But it's used only in helper TraceATN that is not integrated into tests infrastructure
					ParserATNSimulator.trace_atn_sim = true;
				}

				ProfilingATNSimulator profiler = null;
				if (runOptions.profile) {
					profiler = new ProfilingATNSimulator(parser);
					parser.setInterpreter(profiler);
				}
				parser.getInterpreter().setPredictionMode(PredictionMode.valueOf(runOptions.predictionMode.toString()));
				parser.setBuildParseTree(runOptions.buildParseTree);

				Method startRule;
				Object[] args = null;
				try {
					startRule = javaCompiledState.parser.getMethod(runOptions.startRuleName);
				} catch (NoSuchMethodException noSuchMethodException) {
					// try with int _p arg for recursive func
					startRule = javaCompiledState.parser.getMethod(runOptions.startRuleName, int.class);
					args = new Integer[]{0};
				}

				parseTree = (ParserRuleContext) startRule.invoke(parser, args);

				if (runOptions.profile) {
					outStream.println(Arrays.toString(profiler.getDecisionInfo()));
				}

				ParseTreeWalker.DEFAULT.walk(TreeShapeListener.INSTANCE, parseTree);
			}
			else {
				assert tokenStream != null;
				tokenStream.fill();
				for (Object t : tokenStream.getTokens()) {
					outStream.println(t);
				}
				if (runOptions.showDFA) {
					outStream.print(lexer.getInterpreter().getDFA(Lexer.DEFAULT_MODE).toLexerString());
				}
			}

			output = outputStreamHelper.close();
			errors = errorsStreamHelper.close();
		} catch (Exception ex) {
			exception = ex;
		}
		return new JavaExecutedState(javaCompiledState, output, errors, parseTree, exception);
	}

	public JavaRunner() {
		super();
	}

	public JavaRunner(Path tempDir, boolean saveTestDir) {
		super(tempDir, saveTestDir);
	}
}
