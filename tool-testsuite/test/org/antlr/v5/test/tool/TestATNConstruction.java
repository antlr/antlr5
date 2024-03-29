/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.test.tool;

import org.antlr.v5.Tool;
import org.antlr.v5.automata.ATNPrinter;
import org.antlr.v5.automata.LexerATNFactory;
import org.antlr.v5.automata.ParserATNFactory;
import org.antlr.v5.parse.ANTLRParser;
import org.antlr.v5.runtime.core.atn.ATN;
import org.antlr.v5.runtime.core.state.ATNState;
import org.antlr.v5.test.runtime.ErrorQueue;
import org.antlr.v5.test.runtime.RuntimeTestUtils;
import org.antlr.v5.tool.ErrorType;
import org.antlr.v5.tool.Grammar;
import org.antlr.v5.tool.LexerGrammar;
import org.antlr.v5.tool.ast.GrammarAST;
import org.antlr.v5.tool.ast.GrammarRootAST;
import org.antlr.v5.tool.ast.RuleAST;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestATNConstruction {
	@Test public void testA() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A;");
		String expecting =
			"RuleStart_a_0-A->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s2\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testAB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A B ;");
		String expecting =
			"RuleStart_a_0-A->s2\n" +
			"s2-B->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s3\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testAorB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A | B {;} ;");
		String expecting =
			"RuleStart_a_0->BlockStart_5\n" +
				"BlockStart_5->s2\n" +
				"BlockStart_5->s3\n" +
				"s2-A->BlockEnd_6\n" +
				"s3-B->s4\n" +
				"BlockEnd_6->RuleStop_a_1\n" +
				"s4-action_0:-1->BlockEnd_6\n" +
				"RuleStop_a_1-EOF->s7\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testSetAorB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A | B ;");
		String expecting =
			"RuleStart_a_0-{A, B}->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s2\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testLexerIsntSetMultiCharString() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : ('0x' | '0X') ;");
		String expecting =
			"s0->RuleStart_A_1\n" +
				"RuleStart_A_1->BlockStart_7\n" +
				"BlockStart_7->s3\n" +
				"BlockStart_7->s5\n" +
				"s3-'0'->s4\n" +
				"s5-'0'->s6\n" +
				"s4-'x'->BlockEnd_8\n" +
				"s6-'X'->BlockEnd_8\n" +
				"BlockEnd_8->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testRange() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : 'a'..'c' ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-'a'..'c'->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testCharSet() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : [abc] ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-{97..99}->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testCharSetRange() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : [a-c] ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-{97..99}->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testCharSetUnicodeBMPEscape() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : [\\uABCD] ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-43981->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testCharSetUnicodeBMPEscapeRange() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : [a-c\\uABCD-\\uABFF] ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-{97..99, 43981..44031}->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testCharSetUnicodeSMPEscape() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : [\\u{10ABCD}] ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-1092557->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testCharSetUnicodeSMPEscapeRange() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : [a-c\\u{10ABCD}-\\u{10ABFF}] ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-{97..99, 1092557..1092607}->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testCharSetUnicodePropertyEscape() throws Exception {
		// The Gothic script is long dead and unlikely to change (which would
		// cause this test to fail)
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : [\\p{Gothic}] ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-{66352..66378}->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testCharSetUnicodePropertyInvertEscape() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : [\\P{Gothic}] ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-{0..66351, 66379..1114111}->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testCharSetUnicodeMultiplePropertyEscape() throws Exception {
		// Ditto the Mahajani script. Not going to change soon. I hope.
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : [\\p{Gothic}\\p{Mahajani}] ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-{66352..66378, 69968..70006}->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testCharSetUnicodePropertyOverlap() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : [\\p{ASCII_Hex_Digit}\\p{Hex_Digit}] ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
			"RuleStart_A_1-{48..57, 65..70, 97..102, 65296..65305, 65313..65318, 65345..65350}->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testRangeOrRange() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar P;\n"+
			"A : ('a'..'c' 'h' | 'q' 'j'..'l') ;"
		);
		String expecting =
			"s0->RuleStart_A_1\n" +
				"RuleStart_A_1->BlockStart_7\n" +
				"BlockStart_7->s3\n" +
				"BlockStart_7->s5\n" +
				"s3-'a'..'c'->s4\n" +
				"s5-'q'->s6\n" +
				"s4-'h'->BlockEnd_8\n" +
				"s6-'j'..'l'->BlockEnd_8\n" +
				"BlockEnd_8->RuleStop_A_2\n";
		checkTokensRule(g, null, expecting);
	}
	@Test public void testStringLiteralInParser() throws Exception {
		Grammar g = new Grammar(
			"grammar P;\n"+
			"a : A|'b' ;"
		);
		String expecting =
			"RuleStart_a_0-{'b', A}->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s2\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testABorCD() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A B | C D;");
		String expecting =
			"RuleStart_a_0->BlockStart_6\n" +
				"BlockStart_6->s2\n" +
				"BlockStart_6->s4\n" +
				"s2-A->s3\n" +
				"s4-C->s5\n" +
				"s3-B->BlockEnd_7\n" +
				"s5-D->BlockEnd_7\n" +
				"BlockEnd_7->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s8\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testbA() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : b A ;\n"+
			"b : B ;");
		String expecting =
			"RuleStart_a_0-b->RuleStart_b_2\n" +
			"s4-A->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s5\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
		expecting =
			"RuleStart_b_2-B->RuleStop_b_3\n" +
			"RuleStop_b_3->s4\n";
		RuntimeTestUtils.checkRuleATN(g, "b", expecting);
	}
	@Test public void testFollow() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : b A ;\n"+
			"b : B ;\n"+
			"c : b C;");
		String expecting =
			"RuleStart_b_2-B->RuleStop_b_3\n" +
			"RuleStop_b_3->s6\n" +
			"RuleStop_b_3->s7\n";
		RuntimeTestUtils.checkRuleATN(g, "b", expecting);
	}
	@Test public void testAorEpsilon() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A | ;");
		String expecting =
			"RuleStart_a_0->BlockStart_3\n" +
			"BlockStart_3->s2\n" +
			"BlockStart_3->BlockEnd_4\n" +
			"s2-A->BlockEnd_4\n" +
			"BlockEnd_4->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s5\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testAOptional() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A?;");
		String expecting =
			"RuleStart_a_0->BlockStart_3\n" +
				"BlockStart_3->s2\n" +
				"BlockStart_3->BlockEnd_4\n" +
				"s2-A->BlockEnd_4\n" +
				"BlockEnd_4->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s5\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testAorBoptional() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A{;}|B)?;");
		String expecting =
			"RuleStart_a_0->BlockStart_5\n" +
				"BlockStart_5->s2\n" +
				"BlockStart_5->s4\n" +
				"BlockStart_5->BlockEnd_6\n" +
				"s2-A->s3\n" +
				"s4-B->BlockEnd_6\n" +
				"BlockEnd_6->RuleStop_a_1\n" +
				"s3-action_0:-1->BlockEnd_6\n" +
				"RuleStop_a_1-EOF->s7\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testSetAorBoptional() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A|B)?;");
		String expecting =
			"RuleStart_a_0->BlockStart_3\n" +
				"BlockStart_3->s2\n" +
				"BlockStart_3->BlockEnd_4\n" +
				"s2-{A, B}->BlockEnd_4\n" +
				"BlockEnd_4->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s5\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testAorBthenC() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A | B) C;");
		String expecting =
			"RuleStart_a_0-{A, B}->s2\n" +
			"s2-C->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s3\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testAplus() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A+;");
		String expecting =
			"RuleStart_a_0->PlusBlockStart_2\n" +
			"PlusBlockStart_2-A->BlockEnd_3\n" +
			"BlockEnd_3->PlusLoopBack_4\n" +
			"PlusLoopBack_4->PlusBlockStart_2\n" +
			"PlusLoopBack_4->s5\n" +
			"s5->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s6\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testAplusSingleAltHasPlusASTPointingAtLoopBackState() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"s : a B ;\n" +			// (RULE a (BLOCK (ALT (+ (BLOCK (ALT A))))))
			"a : A+;");
		String expecting =
			"RuleStart_a_2->PlusBlockStart_5\n" +
			"PlusBlockStart_5-A->BlockEnd_6\n" +
			"BlockEnd_6->PlusLoopBack_7\n" +
			"PlusLoopBack_7->PlusBlockStart_5\n" +
			"PlusLoopBack_7->s8\n" +
			"s8->RuleStop_a_3\n" +
			"RuleStop_a_3->s4\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
		// Get all AST -> ATNState relationships. Make sure loopback is covered when no loop entry decision
		List<GrammarAST> ruleNodes = g.ast.getNodesWithType(ANTLRParser.RULE);
		RuleAST a = (RuleAST)ruleNodes.get(1);
		List<GrammarAST> nodesInRule = a.getNodesWithType(null);
		Map<GrammarAST, ATNState> covered = new LinkedHashMap<GrammarAST, ATNState>();
		for (GrammarAST node : nodesInRule) {
			if ( node.atnState != null ) {
				covered.put(node, node.atnState);
			}
		}
		assertEquals("{RULE=2, BLOCK=5, +=7, BLOCK=5, A=5}", covered.toString());
	}
	@Test public void testAorBplus() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A|B{;})+;");
		String expecting =
			"RuleStart_a_0->PlusBlockStart_5\n" +
				"PlusBlockStart_5->s2\n" +
				"PlusBlockStart_5->s3\n" +
				"s2-A->BlockEnd_6\n" +
				"s3-B->s4\n" +
				"BlockEnd_6->PlusLoopBack_7\n" +
				"s4-action_0:-1->BlockEnd_6\n" +
				"PlusLoopBack_7->PlusBlockStart_5\n" +
				"PlusLoopBack_7->s8\n" +
				"s8->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s9\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testAorBorEmptyPlus() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A | B | )+ ;");
		String expecting =
			"RuleStart_a_0->PlusBlockStart_4\n" +
			"PlusBlockStart_4->s2\n" +
			"PlusBlockStart_4->s3\n" +
			"PlusBlockStart_4->BlockEnd_5\n" +
			"s2-A->BlockEnd_5\n" +
			"s3-B->BlockEnd_5\n" +
			"BlockEnd_5->PlusLoopBack_6\n" +
			"PlusLoopBack_6->PlusBlockStart_4\n" +
			"PlusLoopBack_6->s7\n" +
			"s7->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s8\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testEmptyOrEmpty() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : | ;");
		String expecting =
			"RuleStart_a_0->BlockStart_2\n" +
			"BlockStart_2->BlockEnd_3\n" +
			"BlockStart_2->BlockEnd_3\n" +
			"BlockEnd_3->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s4\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testAStar() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : A*;");
		String expecting =
			"RuleStart_a_0->StarLoopEntry_4\n" +
			"StarLoopEntry_4->StarBlockStart_2\n" +
			"StarLoopEntry_4->s5\n" +
			"StarBlockStart_2-A->BlockEnd_3\n" +
			"s5->RuleStop_a_1\n" +
			"BlockEnd_3->StarLoopBack_6\n" +
			"RuleStop_a_1-EOF->s7\n" +
			"StarLoopBack_6->StarLoopEntry_4\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testNestedAstar() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (COMMA ID*)*;");
		String expecting =
			"RuleStart_a_0->StarLoopEntry_9\n" +
			"StarLoopEntry_9->StarBlockStart_7\n" +
			"StarLoopEntry_9->s10\n" +
			"StarBlockStart_7-COMMA->StarLoopEntry_4\n" +
			"s10->RuleStop_a_1\n" +
			"StarLoopEntry_4->StarBlockStart_2\n" +
			"StarLoopEntry_4->s5\n" +
			"RuleStop_a_1-EOF->s12\n" +
			"StarBlockStart_2-ID->BlockEnd_3\n" +
			"s5->BlockEnd_8\n" +
			"BlockEnd_3->StarLoopBack_6\n" +
			"BlockEnd_8->StarLoopBack_11\n" +
			"StarLoopBack_6->StarLoopEntry_4\n" +
			"StarLoopBack_11->StarLoopEntry_9\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testAorBstar() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : (A | B{;})* ;");
		String expecting =
			"RuleStart_a_0->StarLoopEntry_7\n" +
				"StarLoopEntry_7->StarBlockStart_5\n" +
				"StarLoopEntry_7->s8\n" +
				"StarBlockStart_5->s2\n" +
				"StarBlockStart_5->s3\n" +
				"s8->RuleStop_a_1\n" +
				"s2-A->BlockEnd_6\n" +
				"s3-B->s4\n" +
				"RuleStop_a_1-EOF->s10\n" +
				"BlockEnd_6->StarLoopBack_9\n" +
				"s4-action_0:-1->BlockEnd_6\n" +
				"StarLoopBack_9->StarLoopEntry_7\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}
	@Test public void testPredicatedAorB() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : {p1}? A | {p2}? B ;");
		String expecting =
			"RuleStart_a_0->BlockStart_6\n" +
				"BlockStart_6->s2\n" +
				"BlockStart_6->s4\n" +
				"s2-pred_0:0->s3\n" +
				"s4-pred_0:1->s5\n" +
				"s3-A->BlockEnd_7\n" +
				"s5-B->BlockEnd_7\n" +
				"BlockEnd_7->RuleStop_a_1\n" +
				"RuleStop_a_1-EOF->s8\n";
		RuntimeTestUtils.checkRuleATN(g, "a", expecting);
	}

	@Test public void testParserRuleRefInLexerRule() throws Exception {
		boolean threwException = false;
		ErrorQueue errorQueue = new ErrorQueue();
		try {
			String gstr =
				"grammar U;\n"+
				"a : A;\n"+
				"A : a;\n";

			Tool tool = new Tool();
			tool.removeListeners();
			tool.addListener(errorQueue);
			assertEquals(0, errorQueue.size());
			GrammarRootAST grammarRootAST = tool.parseGrammarFromString(gstr);
			assertEquals(0, errorQueue.size());
			Grammar g = tool.createGrammar(grammarRootAST);
			assertEquals(0, errorQueue.size());
			g.fileName = "<string>";
			tool.process(g, false);
		}
		catch (Exception e) {
			threwException = true;
			e.printStackTrace();
		}
		System.out.println(errorQueue);
		assertEquals(1, errorQueue.errors.size());
		assertEquals(ErrorType.PARSER_RULE_REF_IN_LEXER_RULE, errorQueue.errors.get(0).getErrorType());
		assertEquals("[a, A]", Arrays.toString(errorQueue.errors.get(0).getArgs()));
		assertTrue(!threwException);
	}

	/** Test for https://github.com/antlr/antlr4/issues/1369
	 *  Repeated edges:

	 RuleStop_e_3->BlockEnd_26
	 RuleStop_e_3->BlockEnd_26
	 RuleStop_e_3->BlockEnd_26

	 * @throws Exception
	 */
	@Test public void testForRepeatedTransitionsToStopState() throws Exception {
		String gstr =
			"grammar T;\n"+
			"\t s : e EOF;\n"+
			"\t e :<assoc=right> e '*' e\n"+
			"\t   |<assoc=right> e '+' e\n"+
			"\t   |<assoc=right> e '?' e ':' e\n"+
			"\t   |<assoc=right> e '=' e\n"+
			"\t   | ID\n"+
			"\t   ;\n"+
			"\t ID : 'a'..'z'+ ;\n"+
			"\t WS : (' '|'\\n') -> skip ;";
		Grammar g = new Grammar(gstr);
		String expecting =
			"RuleStart_e_2-action_1:-1->s5\n" +
			"s5-ID->StarLoopEntry_22\n" +
			"StarLoopEntry_22->StarBlockStart_20\n" +
			"StarLoopEntry_22->s23\n" +
			"StarBlockStart_20->s6\n" +
			"StarBlockStart_20->s9\n" +
			"StarBlockStart_20->s12\n" +
			"StarBlockStart_20->s17\n" +
			"s23->RuleStop_e_3\n" +
			"s6-5 >= _p->s7\n" +
			"s9-4 >= _p->s10\n" +
			"s12-3 >= _p->s13\n" +
			"s17-2 >= _p->s18\n" +
			"RuleStop_e_3->s4\n" +
			"RuleStop_e_3->BlockEnd_21\n" +
			"RuleStop_e_3->BlockEnd_21\n" +
			"RuleStop_e_3->s15\n" +
			"RuleStop_e_3->BlockEnd_21\n" +
			"RuleStop_e_3->BlockEnd_21\n" +
			"s7-'*'->s8\n" +
			"s10-'+'->s11\n" +
			"s13-'?'->s14\n" +
			"s18-'='->s19\n" +
			"s8-e->RuleStart_e_2\n" +
			"s11-e->RuleStart_e_2\n" +
			"s14-e->RuleStart_e_2\n" +
			"s19-e->RuleStart_e_2\n" +
			"BlockEnd_21->StarLoopBack_24\n" +
			"s15-':'->s16\n" +
			"StarLoopBack_24->StarLoopEntry_22\n" +
			"s16-e->RuleStart_e_2\n";
		RuntimeTestUtils.checkRuleATN(g, "e", expecting);
	}


/*
	@Test public void testMultiplePredicates() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : {p1}? {p1a}? A | {p2}? B | {p3} b;\n" +
			"b : {p4}? B ;");
		String expecting =
			"\n";
		checkRule(g, "a", expecting);
	}
	@Test public void testSets() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"a : ( A | B )+ ;\n" +
			"b : ( A | B{;} )+ ;\n" +
			"c : (A|B) (A|B) ;\n" +
			"d : ( A | B )* ;\n" +
			"e : ( A | B )? ;");
		String expecting =
			"\n";
		checkRule(g, "a", expecting);
		expecting =
			"\n";
		checkRule(g, "b", expecting);
		expecting =
			"\n";
		checkRule(g, "c", expecting);
		expecting =
			"\n";
		checkRule(g, "d", expecting);
		expecting =
			"\n";
		checkRule(g, "e", expecting);
	}
	@Test public void testNotSet() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"tokens { A; B; C; }\n"+
			"a : ~A ;\n");
		String expecting =
			"\n";
		checkRule(g, "a", expecting);
	}
	@Test public void testNotSingletonBlockSet() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"tokens { A; B; C; }\n"+
			"a : ~(A) ;\n");
		String expecting =
			"\n";
		checkRule(g, "a", expecting);
	}
	@Test public void testNotCharSet() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : ~'3' ;\n");
		String expecting =
			"RuleStart_A_1->s5\n" +
			"s5-{'\\u0000'..'2', '4'..'\\uFFFE'}->s6\n" +
			"s6->RuleStop_A_2\n";
		checkRule(g, "A", expecting);
	}
	@Test public void testNotBlockSet() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : ~('3'|'b') ;\n");
		String expecting =
			"\n";
		checkRule(g, "A", expecting);
	}
	@Test public void testNotSetLoop() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : ~('3')* ;\n");
		String expecting =
			"\n";
		checkRule(g, "A", expecting);
	}
	@Test public void testNotBlockSetLoop() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : ~('3'|'b')* ;\n");
		String expecting =
			"\n";
		checkRule(g, "A", expecting);
	}
	@Test public void testLabeledNotSet() throws Exception {
		Grammar g = new Grammar(
			"parser grammar P;\n"+
			"tokens { A; B; C; }\n"+
			"a : t=~A ;\n");
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2-B..C->.s3\n" +
			".s3->:s4\n" +
			":s4-EOF->.s5\n";
		checkRule(g, "a", expecting);
	}
	@Test public void testLabeledNotCharSet() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : t=~'3' ;\n");
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2-{'\\u0000'..'2', '4'..'\\uFFFF'}->.s3\n" +
			".s3->:s4\n" +
			":s4-<EOT>->.s5\n";
		checkRule(g, "A", expecting);
	}
	@Test public void testLabeledNotBlockSet() throws Exception {
		Grammar g = new Grammar(
			"lexer grammar P;\n"+
			"A : t=~('3'|'b') ;\n");
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2-{'\\u0000'..'2', '4'..'a', 'c'..'\\uFFFF'}->.s3\n" +
			".s3->:s4\n" +
			":s4-<EOT>->.s5\n";
		checkRule(g, "A", expecting);
	}
	@Test public void testEscapedCharLiteral() throws Exception {
		Grammar g = new Grammar(
			"grammar P;\n"+
			"a : '\\n';");
		String expecting =
			".s0->.s1\n" +
			".s1->.s2\n" +
			".s2-'\\n'->.s3\n" +
			".s3->:s4\n" +
			":s4-EOF->.s5\n";
		checkRule(g, "a", expecting);
	}
	@Test public void testEscapedStringLiteral() throws Exception {
		Grammar g = new Grammar(
			"grammar P;\n"+
			"a : 'a\\nb\\u0030c\\'';");
		String expecting =
			"RuleStart_a_0->s2\n" +
			"s2-'a\\nb\\u0030c\\''->s3\n" +
			"s3->RuleStop_a_1\n" +
			"RuleStop_a_1-EOF->s4\n";
		checkRule(g, "a", expecting);
	}
	*/

	@Test public void testDefaultMode() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' ;\n" +
			"X : 'x' ;\n" +
			"mode FOO;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		String expecting =
			"s0->RuleStart_A_2\n" +
			"s0->RuleStart_X_4\n" +
			"RuleStart_A_2-'a'->RuleStop_A_3\n" +
			"RuleStart_X_4-'x'->RuleStop_X_5\n";
		checkTokensRule(g, "DEFAULT_MODE", expecting);
	}

	@Test public void testMode() throws Exception {
		LexerGrammar g = new LexerGrammar(
			"lexer grammar L;\n"+
			"A : 'a' ;\n" +
			"X : 'x' ;\n" +
			"mode FOO;\n" +
			"B : 'b' ;\n" +
			"C : 'c' ;\n");
		String expecting =
			"s1->RuleStart_B_6\n" +
			"s1->RuleStart_C_8\n" +
			"RuleStart_B_6-'b'->RuleStop_B_7\n" +
			"RuleStart_C_8-'c'->RuleStop_C_9\n";
		checkTokensRule(g, "FOO", expecting);
	}
	void checkTokensRule(LexerGrammar g, String modeName, String expecting) {
//		if ( g.ast!=null && !g.ast.hasErrors ) {
//			System.out.println(g.ast.toStringTree());
//			Tool antlr = new Tool();
//			SemanticPipeline sem = new SemanticPipeline(g);
//			sem.process();
//			if ( g.getImportedGrammars()!=null ) { // process imported grammars (if any)
//				for (Grammar imp : g.getImportedGrammars()) {
//					antlr.processNonCombinedGrammar(imp);
//				}
//			}
//		}
		if ( modeName==null ) modeName = "DEFAULT_MODE";
		if ( g.modes.get(modeName)==null ) {
			System.err.println("no such mode "+modeName);
			return;
		}
		ParserATNFactory f = new LexerATNFactory(g);
		ATN nfa = f.createATN();
		ATNState startState = nfa.getModeNameToStartState().get(modeName);
		ATNPrinter serializer = new ATNPrinter(g, startState);
		String result = serializer.asString();
		//System.out.print(result);
		assertEquals(expecting, result);
	}
}
