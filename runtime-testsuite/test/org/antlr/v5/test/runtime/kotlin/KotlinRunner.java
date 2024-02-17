package org.antlr.v5.test.runtime.kotlin;

import org.antlr.v5.runtime.core.CommonTokenStream;
import org.antlr.v5.runtime.core.Lexer;
import org.antlr.v5.runtime.core.Parser;
import org.antlr.v5.runtime.core.atn.ParserATNSimulator;
import org.antlr.v5.runtime.core.atn.PredictionMode;
import org.antlr.v5.runtime.core.atn.ProfilingATNSimulator;
import org.antlr.v5.runtime.core.context.ParserRuleContext;
import org.antlr.v5.runtime.core.tree.ParseTree;
import org.antlr.v5.runtime.kotlin.DiagnosticErrorListener;
import org.antlr.v5.runtime.kotlin.tree.ParseTreeWalker;
import org.antlr.v5.test.runtime.JvmRunner;
import org.antlr.v5.test.runtime.RunOptions;
import org.antlr.v5.test.runtime.kotlin.helpers.*;
import org.antlr.v5.test.runtime.states.*;
import org.antlr.v5.test.runtime.states.jvm.KotlinCompiledState;
import org.antlr.v5.test.runtime.states.jvm.KotlinExecutedState;
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments;
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer;
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector;
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler;
import org.jetbrains.kotlin.config.Services;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.antlr.v5.test.runtime.JvmRunner.InMemoryStreamHelper.initialize;

public class KotlinRunner extends JvmRunner<Lexer, Parser> {
	private final static DiagnosticErrorListener DiagnosticErrorListenerInstance = new DiagnosticErrorListener();
	private final static K2JVMCompiler compiler = new K2JVMCompiler();
	private final static PrintingMessageCollector messageCollector = new PrintingMessageCollector(java.lang.System.out, MessageRenderer.WITHOUT_PATHS, false);

	@Override
	public String getLanguage() { return "Kotlin"; }

	@Override
	protected String getCompilerName() { return "kotlinc"; }

	@Override
	protected String getRecognizerSuperTypeStartMarker() { return ") : "; }

	@Override
	protected String getRecognizerSuperTypeEndMarker() { return "(input) {"; }

	@Override
	protected void compileClassFiles(RunOptions runOptions, GeneratedState generatedState) {
		K2JVMCompilerArguments arguments = new K2JVMCompilerArguments();
		arguments.setFreeArgs(new ArrayList<>(List.of(getTempDirPath())));
		arguments.setDestination(getTempDirPath());
		arguments.setClasspath(getFullClassPath());
		arguments.setNoReflect(true);
		arguments.setNoStdlib(true);
		// TODO: remove the next line once K2 supports scripts in LightTree mode. Currently the compiler warns
		// `scripts are not yet supported with K2 in LightTree mode, consider using K1 or disable LightTree mode with -Xuse-fir-lt=false`
		arguments.setUseFirLT(false);

		compiler.execImpl(messageCollector, Services.EMPTY, arguments);
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

			GeneratedState generatedState = (GeneratedState) compiledState.previousState;

			CommonTokenStream tokenStream;
			RuntimeTestLexer lexer;
			if (generatedState.lexerName != null) {
				lexer = (RuntimeTestLexer) kotlinCompiledState.initializeLexer(runOptions.input);
				lexer.setOutStream(outStream);
				lexer.removeErrorListeners();
				lexer.addErrorListener(errorListener);
				tokenStream = new CommonTokenStream(lexer);
			} else {
				lexer = null;
				tokenStream = null;
			}

			if (generatedState.parserName != null) {
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
