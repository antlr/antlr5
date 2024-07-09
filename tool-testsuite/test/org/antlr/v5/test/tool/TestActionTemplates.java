package org.antlr.v5.test.tool;

import org.antlr.v5.test.runtime.RunOptions;
import org.antlr.v5.test.runtime.java.JavaRunner;
import org.antlr.v5.test.runtime.states.ExecutedState;
import org.antlr.v5.test.runtime.states.GeneratedState;
import org.antlr.v5.test.runtime.states.State;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.antlr.v5.test.runtime.FileUtils.writeFile;
import static org.antlr.v5.test.tool.ToolTestUtils.createExecOptionsForJavaToolTests;
import static org.junit.jupiter.api.Assertions.*;

public class TestActionTemplates {
	@Test void testIncorrectActionTemplateGroupExtension(@TempDir Path tempDir) {
		String actionTemplates = tempDir.resolve("Java.st").toString();

		writeFile(actionTemplates, "", "UTF-8");

		String grammar =
			"lexer grammar L;" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, "Java.st");

		assertInstanceOf(GeneratedState.class, state, state.getErrorMessage());

		GeneratedState generated = (GeneratedState) state;

		assertTrue(generated.containsErrors());

		assertEquals(
			"State: Generate; \n" +
			"error(208):  error reading action templates file Java.st: " +
			"Group file names must end in .stg: " + actionTemplates + "\n",
			generated.getErrorMessage());
	}

	@Test void testActionTemplateFileMissing(@TempDir Path tempDir) {
		String grammar =
			"lexer grammar L;" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, "Java.stg");

		assertInstanceOf(GeneratedState.class, state, state.getErrorMessage());

		GeneratedState generated = (GeneratedState) state;

		assertTrue(generated.containsErrors());

		assertEquals(
			"State: Generate; \n" +
			"error(206):  cannot find action templates file Java.stg given for L\n",
			generated.getErrorMessage());
	}

	@Test void testUnlexableActionTemplate(@TempDir Path tempDir) {
		writeActionTemplatesFile(tempDir, "writeln(s) ::= <<outStream.println(\"<s>\");>>");

		String grammar =
			"lexer grammar L;\n" +
			"I : '0'..'9'+ {<¢>} ;\n" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, "Java.stg");

		assertInstanceOf(GeneratedState.class, state, state.getErrorMessage());

		GeneratedState generated = (GeneratedState) state;

		assertTrue(generated.containsErrors());

		assertEquals(
			"State: Generate; \n" +
			"error(211): L.g4:2:14: error compiling action template: 2:16: invalid character '¢'\n" +
			"error(211): L.g4:2:14: error compiling action template: 2:14: this doesn't look like a template: \" <¢> \"\n",
			generated.getErrorMessage());
	}

	@Test void testUnlexableMultilineActionTemplate(@TempDir Path tempDir) {
		writeActionTemplatesFile(tempDir, "writeln(s) ::= <<outStream.println(\"<s>\");>>");

		String grammar =
			"lexer grammar L;\n" +
			"I : '0'..'9'+ {\n" +
			"  <¢>\n" +
			"};\n" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, "Java.stg");

		assertInstanceOf(GeneratedState.class, state, state.getErrorMessage());

		GeneratedState generated = (GeneratedState) state;

		assertTrue(generated.containsErrors());

		assertEquals(
			"State: Generate; \n" +
			"error(211): L.g4:2:14: error compiling action template: 3:3: invalid character '¢'\n" +
			"error(211): L.g4:2:14: error compiling action template: 3:0: mismatched input '  ' expecting EOF\n",
			generated.getErrorMessage());
	}

	@Test void testInvalidActionTemplateGroup(@TempDir Path tempDir) {
		String actionTemplates = tempDir.resolve("Java.stg").toString();

		writeActionTemplatesFile(tempDir, "writeln(s) := <<outStream.println(\"<s>\");>>");

		String grammar =
			"lexer grammar L;\n" +
			"I : '0'..'9'+ {<writeln(\"I\")>} ;\n" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, "Java.stg");

		GeneratedState generated = (GeneratedState) state;

		assertTrue(generated.containsErrors());

		assertEquals(
			"State: Generate; \n" +
			"error(209):  error compiling action templates file "+ actionTemplates +": Java.stg 1:11: mismatched input ':' expecting '::='\n" +
			"error(212): L.g4:2:14: error rendering action template: 2:16: no such template: /writeln\n",
			generated.getErrorMessage());
	}

	@Test void testInvalidActionTemplate(@TempDir Path tempDir) {
		writeActionTemplatesFile(tempDir, "writeln(s) ::= <<outStream.println(\"<s>\");>>");

		String grammar =
			"lexer grammar L;\n" +
			"I : '0'..'9'+ {<writeln(\"I\")} ;\n" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, "Java.stg");

		GeneratedState generated = (GeneratedState) state;

		assertTrue(generated.containsErrors());

		assertEquals(
			"State: Generate; \n" +
			"error(211): L.g4:2:14: error compiling action template: 2:29: premature EOF\n",
			generated.getErrorMessage());
	}

	@Test
	void testInvalidMultilineActionTemplate(@TempDir Path tempDir) {
		writeActionTemplatesFile(tempDir, "writeln(s) ::= <<outStream.println(\"<s>\");>>");

		String grammar =
			"lexer grammar L;\n" +
			"I : '0'..'9'+ {\n" +
			"  <writeln(\"I\")\n" +
			"};\n" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, "Java.stg");

		GeneratedState generated = (GeneratedState) state;

		assertTrue(generated.containsErrors());

		assertEquals(
			"State: Generate; \n" +
			"error(211): L.g4:2:14: error compiling action template: 4:1: premature EOF\n",
			generated.getErrorMessage());
	}

	@Test
	void testValidActionTemplate(@TempDir Path tempDir) {
		writeActionTemplatesFile(tempDir, "writeln(s) ::= <<outStream.println(\"<s>\");>>");

		String grammar =
			"lexer grammar L;\n" +
			"I : '0'..'9'+ {<writeln(\"I\")>} ;\n" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, "Java.stg");

		// Should have identical output to TestLexerActions.testActionExecutedInDFA
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<1>,1:0]\n" +
			"[@1,3:4='34',<1>,1:3]\n" +
			"[@2,5:4='<EOF>',<-1>,1:5]\n";

		assertInstanceOf(ExecutedState.class, state, state.getErrorMessage());

		assertEquals(expecting, ((ExecutedState) state).output);
	}

	@Test
	void testValidMultilineActionTemplate(@TempDir Path tempDir) {
		writeActionTemplatesFile(tempDir, "writeln(s) ::= <<outStream.println(\"<s>\");>>");

		String actionTemplates = tempDir.resolve("Java.stg").toString();

		String grammar =
			"lexer grammar L;\n" +
			"I : '0'..'9'+ {\n" +
			"  <writeln(\"I\")>\n" +
			"};\n" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, actionTemplates);

		// Should have identical output to TestLexerActions.testActionExecutedInDFA
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<1>,1:0]\n" +
			"[@1,3:4='34',<1>,1:3]\n" +
			"[@2,5:4='<EOF>',<-1>,1:5]\n";

		assertInstanceOf(ExecutedState.class, state, state.getErrorMessage());

		assertEquals(expecting, ((ExecutedState) state).output);
	}

	@Test
	void testActionTemplateHeader(@TempDir Path tempDir) {
		String actionTemplates =
			"normalizerImports() ::= <<\n" +
			"import java.text.Normalizer;\n" +
			"import java.text.Normalizer.Form;\n" +
			">>\n" +
			"normalize(s) ::= <<Normalizer.normalize(<s>, Form.NFKC)>>\n" +
			"getText() ::= <<getText()>>\n" +
			"setText(s) ::= <<setText(<s>);>>";

		writeActionTemplatesFile(tempDir, actionTemplates);

		String grammar =
			"lexer grammar L;\n" +
			"@lexer::header {\n" +
			"<normalizerImports()>\n" +
			"}\n" +
			"ID : (ID_START ID_CONTINUE* | '_' ID_CONTINUE+) { <setText(normalize(getText()))> } ;\n" +
			"ID_START : [\\p{XID_Start}] ;\n" +
			"ID_CONTINUE: [\\p{XID_Continue}] ;\n" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "This _is \ufb01ne", tempDir, "Java.stg");

		String expecting =
			"[@0,0:3='This',<1>,1:0]\n" +
			"[@1,5:7='_is',<1>,1:5]\n" +
			"[@2,9:11='fine',<1>,1:9]\n" +
			"[@3,12:11='<EOF>',<-1>,1:12]\n";

		assertInstanceOf(ExecutedState.class, state, state.getErrorMessage());

		assertEquals(expecting, ((ExecutedState) state).output);
	}

	@Test
	void testActionTemplateSemanticPredicate(@TempDir Path tempDir) {
		String actionTemplates = "pred() ::= <<true>>";

		writeActionTemplatesFile(tempDir, actionTemplates);

		String actionTemplatesFile = tempDir.resolve("Java.stg").toString();

		String grammar =
			"grammar P;\n" +
			"file : atom EOF ;\n" +
			"atom : scientific | { <pred()> }? variable ;\n" +
			"variable: VARIABLE ;\n" +
			"scientific: SCIENTIFIC_NUMBER ;\n" +
			"VARIABLE : VALID_ID_START VALID_ID_CHAR* ;\n" +
			"SCIENTIFIC_NUMBER : NUMBER (E SIGN? UNSIGNED_INTEGER)? ;\n" +
			"fragment VALID_ID_START : ('a' .. 'z') | ('A' .. 'Z') | '_' ;\n" +
			"fragment VALID_ID_CHAR : VALID_ID_START | ('0' .. '9') ;\n" +
			"fragment NUMBER : ('0' .. '9') + ('.' ('0' .. '9') +)? ;\n" +
			"fragment UNSIGNED_INTEGER : ('0' .. '9')+ ;\n" +
			"fragment E : 'E' | 'e' ;\n" +
			"fragment SIGN : ('+' | '-') ;\n" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execParser(grammar, "Bla", tempDir, "file", actionTemplatesFile);

		assertInstanceOf(ExecutedState.class, state, state.getErrorMessage());

		ExecutedState executedState = (ExecutedState) state;

		// We can never match the input unless pred() expands to true
		assertEquals("", executedState.output);
		assertEquals("", executedState.errors);
	}

	@Test
	void testValidActionTemplateInLibDir(@TempDir Path tempDir, @TempDir Path libDir) {
		writeActionTemplatesFile(libDir, "writeln(s) ::= <<outStream.println(\"<s>\");>>");

		String actionTemplates = "Java.stg";

		String grammar =
			"lexer grammar L;\n" +
			"I : '0'..'9'+ {<writeln(\"I\")>} ;\n" +
			"WS : (' '|'\\n') -> skip ;";

		State state = execLexer(grammar, "34 34", tempDir, libDir, actionTemplates);

		// Should have identical output to TestLexerActions.testActionExecutedInDFA
		String expecting =
			"I\n" +
			"I\n" +
			"[@0,0:1='34',<1>,1:0]\n" +
			"[@1,3:4='34',<1>,1:3]\n" +
			"[@2,5:4='<EOF>',<-1>,1:5]\n";

		assertInstanceOf(ExecutedState.class, state, state.getErrorMessage());

		assertEquals(expecting, ((ExecutedState) state).output);
	}

	void writeActionTemplatesFile(Path tempDir, String template) {
		writeFile(tempDir.resolve("Java.stg").toString(), template, "UTF-8");
	}

	State execParser(String grammarStr, String input, Path tempDir, String startRule, String actionTemplates) {
		RunOptions runOptions = createExecOptionsForJavaToolTests(grammarStr, false, true, startRule, input, null, actionTemplates, false, false);
		try (JavaRunner runner = new JavaRunner(tempDir, false)) {
			return runner.run(runOptions);
		}
	}

	State execLexer(String grammarStr, String input, Path tempDir, String actionTemplates) {
		RunOptions runOptions = createExecOptionsForJavaToolTests(grammarStr, false, true, null, input, null, actionTemplates, false, false);
		try (JavaRunner runner = new JavaRunner(tempDir, false)) {
			return runner.run(runOptions);
		}
	}

	State execLexer(String grammarStr, String input, Path tempDir, Path libDir, String actionTemplates) {
		RunOptions runOptions = createExecOptionsForJavaToolTests(grammarStr, false, true, null, input, libDir.toString(), actionTemplates, false, false);
		try (JavaRunner runner = new JavaRunner(tempDir, false)) {
			return runner.run(runOptions);
		}
	}
}
