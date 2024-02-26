/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.test.runtime;

import org.antlr.v5.Tool;
import org.antlr.v5.test.runtime.states.GeneratedState;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.antlr.v5.test.runtime.FileUtils.writeFile;

public class Generator {
	private final static Pattern grammarHeaderRegex = Pattern.compile("^((?<grammarType>(parser|lexer))\\s+)?grammar\\s+(?<grammarName>[^;\\s]+)\\s*;");

	public static GeneratedState generate(RunOptions runOptions, String language, String workingDirectory, String[] extraOptions) {
		if (runOptions.slaveGrammars != null) {
			for (String grammar : runOptions.slaveGrammars) {
				GrammarFile slaveGrammarFile = parseGrammarFile(grammar);
				writeFile(workingDirectory, slaveGrammarFile.grammarName + ".g4", slaveGrammarFile.content);
			}
		}

		List<GrammarFile> grammarFiles = new ArrayList<>();
		for (String grammar : runOptions.grammars) {
			grammarFiles.add(parseGrammarFile(grammar));
		}

		GrammarFile mainFile = grammarFiles.get(0);
		String mainGrammarName = mainFile.grammarName;

		final List<String> options = new ArrayList<>();

		for (GrammarFile grammarFile : grammarFiles) {
			Path fullPath = Paths.get(workingDirectory, grammarFile.grammarName + ".g4");
			writeFile(fullPath.toString(), grammarFile.content, "utf-8");

			if (grammarFile == mainFile) continue;

			// Generate dependent recognizers at first
			options.add(grammarFile.grammarName + ".g4");
		}

		// Generate the main recognizer at last
		options.add(mainFile.grammarName + ".g4");

		if (runOptions.useVisitor) {
			options.add("-visitor");
		}

		String superClass = runOptions.superClass;
		if (runOptions.superClass != null) {
			superClass = runOptions.superClass;
		}

		if (superClass == null && language != null && (language.equals("Java") || language.equals("Kotlin"))) {
			if (mainFile.containsParser()) {
				superClass = JvmRunner.parserHelperFQN.get(language);
			}
			else {
				superClass = JvmRunner.lexerHelperFQN.get(language);
			}
		}

		if (superClass != null) {
			options.add("-DsuperClass=" + superClass);
		}

		if (extraOptions != null) {
			options.addAll(Arrays.asList(extraOptions));
		}

		String[] extraGenerationOptions = runOptions.extraGenerationOptions;
		if (extraGenerationOptions != null) {
			options.addAll(Arrays.asList(extraGenerationOptions));
		}

		String outputDirectory;

		if (!options.contains("-o")) {
			outputDirectory = workingDirectory;
		}
		else {
			outputDirectory = options.get(options.indexOf("-o") + 1);
		}
		options.add("-o");
		options.add(outputDirectory);

		if (!options.contains("-lib")) {
			options.add("-lib");
			options.add(workingDirectory);
		}
		if (!options.contains("-encoding")) {
			options.add("-encoding");
			options.add("UTF-8");
		}
		if (!options.contains("-Dlanguage=") && language != null) {
			options.add("-Dlanguage=" + language);
		}

		Tool antlr = new Tool(options.toArray(new String[0]));
		ErrorQueue errorQueue = new ErrorQueue(antlr);
		antlr.inputDirectory = new File(workingDirectory);
		antlr.addListener(errorQueue);
		antlr.processGrammarsOnCommandLine();

		final LexerParserName lexerParserName;
		List<GeneratedFile> generatedFiles = new ArrayList<>();

		language = language != null ? language : "Java";
		if (errorQueue.errors.isEmpty()) {
			lexerParserName = getLexerParserName(mainFile, grammarFiles, language, outputDirectory);
			generatedFiles = getGeneratedFiles(runOptions, language, mainGrammarName, outputDirectory, lexerParserName);
		} else {
			lexerParserName = new LexerParserName(null, null);
		}

		return new GeneratedState(grammarFiles, errorQueue, lexerParserName.lexerName, lexerParserName.parserName, generatedFiles, null);
	}

	private static GrammarFile parseGrammarFile(String input) {
		Matcher matcher = grammarHeaderRegex.matcher(input);

		GrammarFile.Type grammarType = GrammarFile.Type.Combined;
		String grammarName = null;

		if (matcher.find()) {
			String grammarTypeValue = matcher.group("grammarType");
			grammarName = matcher.group("grammarName");

			if (grammarTypeValue != null) {
				grammarType = grammarTypeValue.equals("parser") ? GrammarFile.Type.Parser : GrammarFile.Type.Lexer;
			}
		}

		return new GrammarFile(grammarName, grammarType, input);
	}

	private static LexerParserName getLexerParserName(GrammarFile mainFile, List<GrammarFile> grammarFiles,
													  String language, String outputDirectory) {
		final String lexerName;
		final String parserName;

		if (mainFile.type == GrammarFile.Type.Combined) {
			String tempLexerName = mainFile.grammarName + "Lexer";
			String generateLexerFileName = tempLexerName + "." + getExtension(language);
			lexerName = Files.exists(new File(outputDirectory, generateLexerFileName).toPath()) ? tempLexerName : null;
			parserName = mainFile.grammarName + "Parser";
		}
		else if (mainFile.type == GrammarFile.Type.Lexer) {
			lexerName = mainFile.grammarName;
			parserName = null;
		}
		else {
			Optional<GrammarFile> lexerFile = grammarFiles.stream().filter(g -> g.type == GrammarFile.Type.Lexer).findFirst();
			lexerName = lexerFile.map(grammarFile -> grammarFile.grammarName).orElse(null);
			parserName = mainFile.grammarName;
		}

		return new LexerParserName(lexerName, parserName);
	}

	private static class LexerParserName {
		public final String lexerName;
		public final String parserName;

		private LexerParserName(String lexerName, String parserName) {
			this.lexerName = lexerName;
			this.parserName = parserName;
		}
	}

	private static List<GeneratedFile> getGeneratedFiles(RunOptions runOptions, String language, String mainGrammarName,
														 String outputDirectory, LexerParserName lexerParserName) {
		List<GeneratedFile> generatedFiles = new ArrayList<>();

		String extensionWithDot = "." + getExtension(language);

		if (lexerParserName.lexerName != null) {
			String generateLexerFileName = lexerParserName.lexerName + extensionWithDot;
			generatedFiles.add(new GeneratedFile(generateLexerFileName, GeneratedFile.Type.Lexer));
		}

		if (lexerParserName.parserName != null) {
			String generatedParserFileName = lexerParserName.parserName + extensionWithDot;
			generatedFiles.add(new GeneratedFile(generatedParserFileName, GeneratedFile.Type.Parser));

			if (runOptions.useListener) {
				generatedFiles.add(new GeneratedFile(mainGrammarName + "Listener" + extensionWithDot, GeneratedFile.Type.Other));
				String baseListenerSuffix = getBaseListenerSuffix(language);
				if (baseListenerSuffix != null) {
					generatedFiles.add(new GeneratedFile(mainGrammarName + baseListenerSuffix + extensionWithDot, GeneratedFile.Type.Other));
				}
			}
			if (runOptions.useVisitor) {
				generatedFiles.add(new GeneratedFile(mainGrammarName + "Visitor" + extensionWithDot, GeneratedFile.Type.Other));
				String baseVisitorSuffix = getBaseVisitorSuffix(language);
				if (baseVisitorSuffix != null) {
					generatedFiles.add(new GeneratedFile(mainGrammarName + baseVisitorSuffix + extensionWithDot, GeneratedFile.Type.Other));
				}
			}
		}

		for (GeneratedFile generatedFile : generatedFiles) {
			if (!Files.exists(new File(outputDirectory, generatedFile.name).toPath())) {
				throw new RuntimeException("Generated file " + generatedFile.name + " should exist");
			}
		}

		return generatedFiles;
	}

	public static String getExtension(String language) {
		switch (language) {
			case "Kotlin":
				return "kt";
			case "JavaScript":
				return "js";
			case "TypeScript":
				return "ts";
			default:
				return language.toLowerCase();
		}
	}

	private static String getBaseListenerSuffix(String language) {
		switch (language) {
			case "JavaScript":
			case "TypeScript":
				return null;
			default:
				return "BaseListener";
		}
	}

	private static String getBaseVisitorSuffix(String language) {
		switch (language) {
			case "JavaScript":
			case "TypeScript":
				return null;
			default:
				return "BaseVisitor";
		}
	}
}
