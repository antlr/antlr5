/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.test.runtime;

import kotlin.Pair;

import java.net.URI;
import java.util.*;

public class RuntimeTestDescriptorParser {
	private final static String NOTES_SECTION_NAME = "notes";
	private final static String GRAMMAR_SECTION_NAME = "grammar";
	private final static String SLAVE_GRAMMAR_SECTION_NAME = "slaveGrammar";
	private final static String START_SECTION_NAME = "start";
	private final static String INPUT_SECTION_NAME = "input";
	private final static String OUTPUT_SECTION_NAME = "output";
	private final static String ERRORS_SECTION_NAME = "errors";
	private final static String FLAGS_SECTION_NAME = "flags";
	private final static String SKIP_SECTION_NAME = "skip";

	private final static Set<String> sections = new HashSet<>(Arrays.asList(
		NOTES_SECTION_NAME,
		GRAMMAR_SECTION_NAME,
		SLAVE_GRAMMAR_SECTION_NAME,
		START_SECTION_NAME,
		INPUT_SECTION_NAME,
		OUTPUT_SECTION_NAME,
		ERRORS_SECTION_NAME,
		FLAGS_SECTION_NAME,
		SKIP_SECTION_NAME
	));

	/**  Read stuff like:
	 [grammar]
	 grammar T;
	 s @after {<DumpDFA()>}
	 : ID | ID {} ;
	 ID : 'a'..'z'+;
	 WS : (' '|'\t'|'\n')+ -> skip ;

	 [grammarName]
	 T

	 [start]
	 s

	 [input]
	 abc

	 [output]
	 Decision 0:
	 s0-ID->:s1^=>1

	 [errors]
	 """line 1:0 reportAttemptingFullContext d=0 (s), input='abc'
	 """

	 Some can be missing like [errors].

	 Get gr names automatically "lexer grammar Unicode;" "grammar T;" "parser grammar S;"

	 Also handle slave grammars:

	 [grammar]
	 grammar M;
	 import S,T;
	 s : a ;
	 B : 'b' ; // defines B from inherited token space
	 WS : (' '|'\n') -> skip ;

	 [slaveGrammar]
	 parser grammar T;
	 a : B {<writeln("\"T.a\"")>};<! hidden by S.a !>

	 [slaveGrammar]
	 parser grammar S;
	 a : b {<writeln("\"S.a\"")>};
	 b : B;
	 */
	public static RuntimeTestDescriptor parse(String name, String text, URI uri) throws RuntimeException {
		String currentField = null;
		StringBuilder currentValue = new StringBuilder();

		List<Pair<String, String>> pairs = new ArrayList<>();
		String[] lines = text.split("\r?\n");

		for (String line : lines) {
			boolean newSection = false;
			String sectionName = null;
			if (line.startsWith("[") && line.length() > 2) {
				sectionName = line.substring(1, line.length() - 1);
				newSection = sections.contains(sectionName);
			}

			if (newSection) {
				if (currentField != null) {
					pairs.add(new Pair<>(currentField, currentValue.toString()));
				}
				currentField = sectionName;
				currentValue.setLength(0);
			}
			else {
				currentValue.append(line);
				currentValue.append("\n");
			}
		}
		pairs.add(new Pair<>(currentField, currentValue.toString()));

		String notes = "";
		List<String> grammars = new ArrayList<>();
		List<String> slaveGrammars = new ArrayList<>();
		String startRule = "";
		String input = "";
		String output = "";
		String errors = "";
		boolean showDFA = false;
		boolean showDiagnosticErrors = false;
		boolean traceATN = false;
		PredictionMode predictionMode = PredictionMode.LL;
		boolean buildParseTree = true;
		String[] skipTargets = new String[0];
		for (Pair<String,String> p : pairs) {
			String section = p.getFirst();
			String value = "";
			if ( p.getSecond()!=null ) {
				value = p.getSecond().trim();
			}
			if ( value.startsWith("\"\"\"") ) {
				value = value.replace("\"\"\"", "");
			}
			else if ( value.indexOf('\n')>=0 ) {
				value = value + "\n"; // if multi line and not quoted, leave \n on end.
			}
			switch (section) {
				case NOTES_SECTION_NAME:
					notes = value;
					break;
				case GRAMMAR_SECTION_NAME:
					grammars.add(value);
					break;
				case SLAVE_GRAMMAR_SECTION_NAME:
					slaveGrammars.add(value);
					break;
				case START_SECTION_NAME:
					startRule = value;
					break;
				case INPUT_SECTION_NAME:
					input = value;
					break;
				case OUTPUT_SECTION_NAME:
					output = value;
					break;
				case ERRORS_SECTION_NAME:
					errors = value;
					break;
				case FLAGS_SECTION_NAME:
					String[] flags = value.split("\n");
					for (String f : flags) {
						String[] parts = f.split("=", 2);
						switch (parts[0]) {
							case "showDFA":
								showDFA = true;
								break;
							case "showDiagnosticErrors":
								showDiagnosticErrors = true;
								break;
							case "traceATN":
								traceATN = true;
								break;
							case "predictionMode":
								predictionMode = PredictionMode.valueOf(parts[1]);
								break;
							case "notBuildParseTree":
								buildParseTree = false;
								break;
							default:
								throw new RuntimeException("Unknown flag: " + parts[0]);
						}
					}
					break;
				case SKIP_SECTION_NAME:
					skipTargets = value.split("\n");
					break;
				default:
					throw new RuntimeException("Unknown descriptor section ignored: "+section);
			}
		}
		return new RuntimeTestDescriptor(name, notes, input, output, errors, startRule,
				grammars.toArray(new String[0]), slaveGrammars.toArray(new String[0]),
				showDiagnosticErrors, traceATN, showDFA, predictionMode, buildParseTree, skipTargets, uri);
	}
}
