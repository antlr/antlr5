package org.antlr.v5.test.runtime;

import org.antlr.v5.test.runtime.states.*;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.antlr.v5.test.runtime.FileUtils.replaceInFile;
import static org.antlr.v5.test.runtime.RuntimeTestUtils.PathSeparator;

public abstract class JvmRunner<TLexer, TParser> extends RuntimeRunner {
	public static final Map<String, String> lexerHelperFQN = new HashMap<>();
	public static final Map<String, String> parserHelperFQN = new HashMap<>();

	private static final Map<String, String> classPath = new HashMap<>();

	static {
		lexerHelperFQN.put("Java", getRuntimeTestRecognizerFQN("Java", true));
		parserHelperFQN.put("Java", getRuntimeTestRecognizerFQN("Java", false));
		lexerHelperFQN.put("Kotlin", getRuntimeTestRecognizerFQN("Kotlin", true));
		parserHelperFQN.put("Kotlin", getRuntimeTestRecognizerFQN("Kotlin", false));
		String originalClassPath = System.getProperty("java.class.path");
		classPath.put("Java", getRuntimeHelpersPath("Java", originalClassPath));
		classPath.put("Kotlin", getRuntimeHelpersPath("Kotlin", originalClassPath));
	}

	public static String getRuntimeTestRecognizerFQN(String language, boolean isLexer) {
		return "org.antlr.v4.test.runtime." + language.toLowerCase() + ".helpers.RuntimeTest" + (isLexer ? "Lexer" : "Parser");
	}

	private static String getRuntimeHelpersPath(String language, String originalClassPath) {
		return PathSeparator + Paths.get(RuntimeTestUtils.runtimeTestsuitePath.toString(),
			"test", "org", "antlr", "v4", "test", "runtime", language.toLowerCase(), "helpers")
			+ PathSeparator + originalClassPath;
	}

	public JvmRunner(Path tempDir, boolean saveTestDir) {
		super(tempDir, saveTestDir);
	}

	public JvmRunner() {
		super();
	}

	protected abstract String getRecognizerSuperTypeStartMarker();

	protected abstract String getRecognizerSuperTypeEndMarker();

	@Override
	protected void writeInputFile(RunOptions runOptions) {}

	@Override
	protected void writeRecognizerFile(RunOptions runOptions) {}

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		String tempTestDir = getTempDirPath();

		List<GeneratedFile> generatedFiles = generatedState.generatedFiles;
		GeneratedFile firstFile = generatedFiles.get(0);

		if (!firstFile.isParser) {
			try {
				fixRecognizerSupertype(firstFile.name);
			} catch (IOException e) {
				return createCompiledState(generatedState, null, null, null, e);
			}
		}

		ClassLoader loader = null;
		Class<? extends TLexer> lexer = null;
		Class<? extends TParser> parser = null;
		Exception exception = null;

		try {
			compileClassFiles(runOptions);

			loader = new URLClassLoader(new URL[]{new File(tempTestDir).toURI().toURL()}, ClassLoader.getSystemClassLoader());
			if (runOptions.lexerName != null) {
				lexer = (Class<? extends TLexer>)loader.loadClass(runOptions.lexerName);
			}
			if (runOptions.parserName != null) {
				parser = (Class<? extends TParser>)loader.loadClass(runOptions.parserName);
			}
		} catch (Exception ex) {
			exception = ex;
		}

		return createCompiledState(generatedState, loader, lexer, parser, exception);
	}

	/*
	  superClass for combined grammar generates the same extends base class for Lexer and Parser
	  So, for lexer it should be replaced on correct base lexer class
	 */
	private void fixRecognizerSupertype(String fileName) throws IOException {
		String recognizerSuperTypeStartMarker = getRecognizerSuperTypeStartMarker();
		String recognizerSuperTypeEndMarker = getRecognizerSuperTypeEndMarker();
		replaceInFile(Paths.get(getTempDirPath(), fileName),
			recognizerSuperTypeStartMarker + parserHelperFQN.get(getLanguage()) + recognizerSuperTypeEndMarker,
			recognizerSuperTypeStartMarker + lexerHelperFQN.get(getLanguage()) + recognizerSuperTypeEndMarker);
	}

	protected abstract void compileClassFiles(RunOptions runOptions) throws Exception;

	protected abstract CompiledState createCompiledState(GeneratedState generatedState, ClassLoader loader,
														 Class<? extends TLexer> lexer, Class<? extends TParser> parser,
														 Exception exception);

	protected String getFullClassPath() {
		return getTempDirPath() + classPath.get(getLanguage());
	}

	protected abstract ExecutedState execute(RunOptions runOptions, CompiledState compiledState);

	public static class InMemoryStreamHelper {
		public final PipedOutputStream pipedOutputStream;
		public final StreamReader streamReader;

		private InMemoryStreamHelper(PipedOutputStream pipedOutputStream, StreamReader streamReader) {
			this.pipedOutputStream = pipedOutputStream;
			this.streamReader = streamReader;
		}

		public static JvmRunner.InMemoryStreamHelper initialize() throws IOException {
			PipedInputStream pipedInputStream = new PipedInputStream();
			PipedOutputStream pipedOutputStream = new PipedOutputStream(pipedInputStream);
			StreamReader stdoutReader = new StreamReader(pipedInputStream);
			stdoutReader.start();
			return new JvmRunner.InMemoryStreamHelper(pipedOutputStream, stdoutReader);
		}

		public String close() throws InterruptedException, IOException {
			pipedOutputStream.close();
			streamReader.join();
			return streamReader.toString();
		}
	}
}
