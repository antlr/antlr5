package org.antlr.v5.test.runtime.kotlin;

import org.antlr.v5.kotlinruntime.*;
import org.antlr.v5.kotlinruntime.atn.PredictionMode;
import org.antlr.v5.kotlinruntime.atn.ProfilingATNSimulator;
import org.antlr.v5.kotlinruntime.tree.ParseTree;
import org.antlr.v5.kotlinruntime.tree.ParseTreeWalker;
import org.antlr.v5.kotlinruntime.atn.ParserATNSimulator;
import org.antlr.v5.test.runtime.JvmRunner;
import org.antlr.v5.test.runtime.RunOptions;
import org.antlr.v5.test.runtime.kotlin.helpers.*;
import org.antlr.v5.test.runtime.states.*;
import org.antlr.v5.test.runtime.states.jvm.KotlinCompiledState;
import org.antlr.v5.test.runtime.states.jvm.KotlinExecutedState;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.antlr.v5.test.runtime.JvmRunner.InMemoryStreamHelper.initialize;
import static org.antlr.v5.test.runtime.RuntimeTestUtils.isWindows;

public class KotlinRunner extends JvmRunner<Lexer, Parser> {
	private final static DiagnosticErrorListener DiagnosticErrorListenerInstance = new DiagnosticErrorListener();

	@Override
	public String getLanguage() { return "Kotlin"; }

	@Override
	protected String getExtension() { return "kt"; }

	@Override
	protected String getCompilerName() { return "kotlinc" + (isWindows() ? ".bat" : ""); }

	@Override
	protected String getRecognizerSuperTypeStartMarker() { return ") : "; }

	@Override
	protected String getRecognizerSuperTypeEndMarker() { return "(input) {"; }

	@Override
	protected void compileClassFiles(RunOptions runOptions) throws Exception {
		runCommand(
			new String[]{
				getCompilerPath(),
				escapeCliArgumentIfNeeded(getTempDirPath()),
				"-cp",
				escapeCliArgumentIfNeeded(getFullClassPath()),
				"-Xskip-prerelease-check",
				"-J-Xmx512m"
			},
			getTempDirPath(),
			"build class files from Kotlin");
	}

	private String escapeCliArgumentIfNeeded(String path) {
		return isWindows() ? '"' + path + '"' : path;
	}

	@Override
	protected CompiledState createCompiledState(GeneratedState generatedState, ClassLoader loader,
												Class<? extends Lexer> lexer, Class<? extends Parser> parser,
												Exception exception) {
		return new KotlinCompiledState(generatedState, loader, lexer, parser, exception);
	}

	@Override
	protected ExecutedState execute(RunOptions runOptions, CompiledState compiledState) {
		KotlinCompiledState kotlinCompiledState = (KotlinCompiledState) compiledState;
		String output = null;
		String errors = null;
		ParseTree parseTree = null;
		Exception exception = null;

		try {
			InMemoryStreamHelper outputStreamHelper = initialize();
			InMemoryStreamHelper errorsStreamHelper = initialize();

			RuntimeTestPrintStream outStream = new RuntimeTestPrintStream(outputStreamHelper.pipedOutputStream);
			CustomStreamErrorListener errorListener = new CustomStreamErrorListener(new PrintStream(errorsStreamHelper.pipedOutputStream));

			CommonTokenStream tokenStream;
			RuntimeTestLexer lexer;
			if (runOptions.lexerName != null) {
				lexer = (RuntimeTestLexer) kotlinCompiledState.initializeLexer(runOptions.input);
				lexer.setOutStream(outStream);
				lexer.removeErrorListeners();
				lexer.addErrorListener(errorListener);
				tokenStream = new CommonTokenStream(lexer);
			} else {
				lexer = null;
				tokenStream = null;
			}

			if (runOptions.parserName != null) {
				RuntimeTestParser parser = (RuntimeTestParser) kotlinCompiledState.initializeParser(tokenStream);
				parser.setOutStream(outStream);
				parser.removeErrorListeners();
				parser.addErrorListener(errorListener);

				if (runOptions.showDiagnosticErrors) {
					parser.addErrorListener(DiagnosticErrorListenerInstance);
				}

				if (runOptions.traceATN) {
					// Setting trace_atn_sim isn't thread-safe,
					// But it's used only in helper TraceATN that is not integrated into tests infrastructure
					ParserATNSimulator.Companion.setTrace_atn_sim(true);
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
					startRule = kotlinCompiledState.parser.getMethod(runOptions.startRuleName);
				} catch (NoSuchMethodException noSuchMethodException) {
					// try with int _p arg for recursive func
					startRule = kotlinCompiledState.parser.getMethod(runOptions.startRuleName, int.class);
					args = new Integer[]{0};
				}

				parseTree = (ParserRuleContext) startRule.invoke(parser, args);

				if (runOptions.profile) {
					outStream.println(Arrays.toString(profiler.getDecisionInfo()));
				}

				ParseTreeWalker.getDEFAULT().walk(TreeShapeListener.Companion.getINSTANCE(), parseTree);
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
		return new KotlinExecutedState(kotlinCompiledState, output, errors, parseTree, exception);
	}

	public KotlinRunner() {
		super();
	}
}
