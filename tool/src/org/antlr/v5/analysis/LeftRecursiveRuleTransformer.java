/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.analysis;

import kotlin.Pair;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.ParserRuleReturnScope;
import org.antlr.runtime.Token;
import org.antlr.v5.Tool;
import org.antlr.v5.misc.OrderedHashMap;
import org.antlr.v5.parse.ANTLRLexer;
import org.antlr.v5.parse.ANTLRParser;
import org.antlr.v5.parse.GrammarASTAdaptor;
import org.antlr.v5.parse.ScopeParser;
import org.antlr.v5.parse.ToolANTLRParser;
import org.antlr.v5.semantics.BasicSemanticChecks;
import org.antlr.v5.semantics.RuleCollector;
import org.antlr.v5.tool.AttributeDict;
import org.antlr.v5.tool.ErrorType;
import org.antlr.v5.tool.Grammar;
import org.antlr.v5.tool.GrammarTransformPipeline;
import org.antlr.v5.tool.LabelElementPair;
import org.antlr.v5.tool.LeftRecursiveRule;
import org.antlr.v5.tool.Rule;
import org.antlr.v5.tool.ast.ActionAST;
import org.antlr.v5.tool.ast.AltAST;
import org.antlr.v5.tool.ast.BlockAST;
import org.antlr.v5.tool.ast.GrammarAST;
import org.antlr.v5.tool.ast.GrammarASTWithOptions;
import org.antlr.v5.tool.ast.GrammarRootAST;
import org.antlr.v5.tool.ast.RuleAST;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Remove left-recursive rule refs, add precedence args to recursive rule refs.
 *  Rewrite rule so we can create ATN.
 *
 *  MODIFIES grammar AST in place.
 */
public class LeftRecursiveRuleTransformer {
	public static final String PRECEDENCE_OPTION_NAME = "p";
	public static final String TOKENINDEX_OPTION_NAME = "tokenIndex";

	public GrammarRootAST ast;
	public Collection<Rule> rules;
	public Grammar g;
	public Tool tool;

	public LeftRecursiveRuleTransformer(GrammarRootAST ast, Collection<Rule> rules, Grammar g) {
		this.ast = ast;
		this.rules = rules;
		this.g = g;
		this.tool = g.tool;
	}

	public void translateLeftRecursiveRules() {
		String language = g.getLanguage();
		// translate all recursive rules
		List<String> leftRecursiveRuleNames = new ArrayList<>();
		for (Rule r : rules) {
			if (!Grammar.isTokenName(r.name)) {
				if (r instanceof LeftRecursiveRule) {
					LeftRecursiveRule leftRecursiveRule = (LeftRecursiveRule) r;
					if (!leftRecursiveRule.ruleInfo.nonConformingLeftRecursion) {
						translateLeftRecursiveRule(ast, leftRecursiveRule, language);
						leftRecursiveRuleNames.add(r.name);
					}
					else {
						// TODO: better given an error that non-conforming left-recursion exists
						tool.errMgr.grammarError(ErrorType.NONCONFORMING_LR_RULE, g.fileName, ((GrammarAST) r.ast.getChild(0)).token, r.name);
					}
				}
			}
		}

		// update all refs to recursive rules to have [0] argument
		for (GrammarAST r : ast.getNodesWithType(ANTLRParser.RULE_REF)) {
			if ( r.getParent().getType()==ANTLRParser.RULE ) continue; // must be rule def
			if ( ((GrammarASTWithOptions)r).getOptionString(PRECEDENCE_OPTION_NAME) != null ) continue; // already has arg; must be in rewritten rule
			if ( leftRecursiveRuleNames.contains(r.getText()) ) {
				// found ref to recursive rule not already rewritten with arg
				((GrammarASTWithOptions)r).setOption(PRECEDENCE_OPTION_NAME, (GrammarAST)new GrammarASTAdaptor().create(ANTLRParser.INT, "0"));
			}
		}
	}

	public void translateLeftRecursiveRule(GrammarRootAST ast,
										   LeftRecursiveRule r,
										   String language)
	{
		GrammarAST prevRuleAST = r.ast;

		LeftRecursiveRuleAnalyzer leftRecursiveRuleAnalyzer =
			new LeftRecursiveRuleAnalyzer(tool, r, prevRuleAST.g.tokenStream, language);
		leftRecursiveRuleAnalyzer.analyze();

		// replace old rule's AST; first create text of altered rule
		GrammarAST RULES = (GrammarAST)ast.getFirstChildWithType(ANTLRParser.RULES);
		String newRuleText = leftRecursiveRuleAnalyzer.getArtificialOpPrecRule();
		// now parse within the context of the grammar that originally created
		// the AST we are transforming. This could be an imported grammar so
		// we cannot just reference this.g because the role might come from
		// the imported grammar and not the root grammar (this.g)
		RuleAST t = parseArtificialRule(prevRuleAST.g, newRuleText);

		// reuse the name token from the original AST since it refers to the proper source location in the original grammar
		((GrammarAST)t.getChild(0)).token = ((GrammarAST)prevRuleAST.getChild(0)).getToken();

		// update grammar AST and set rule's AST.
		RULES.setChild(prevRuleAST.getChildIndex(), t);
		r.ast = t;

		// Reduce sets in newly created rule tree
		GrammarTransformPipeline transform = new GrammarTransformPipeline(g, g.tool);
		transform.reduceBlocksToSets(r.ast);
		transform.expandParameterizedLoops(r.ast);

		// Rerun semantic checks on the new rule
		RuleCollector ruleCollector = new RuleCollector(g);
		ruleCollector.visit(t, "rule");
		BasicSemanticChecks basics = new BasicSemanticChecks(g, ruleCollector);
		// disable the assoc element option checks because they are already
		// handled for the pre-transformed rule.
		basics.checkAssocElementOption = false;
		basics.visit(t, "rule");

		// track recursive alt info for codegen
		r.recPrimaryAlts = new ArrayList<>();
		r.recPrimaryAlts.addAll(leftRecursiveRuleAnalyzer.prefixAndOtherAlts);
		if (r.recPrimaryAlts.isEmpty()) {
			tool.errMgr.grammarError(ErrorType.NO_NON_LR_ALTS, g.fileName, ((GrammarAST)r.ast.getChild(0)).getToken(), r.name);
		}

		r.recOpAlts = new OrderedHashMap<>();
		r.recOpAlts.putAll(leftRecursiveRuleAnalyzer.binaryAlts);
		r.recOpAlts.putAll(leftRecursiveRuleAnalyzer.suffixAlts);

		// walk alt info records and set their altAST to point to appropriate ALT subtree
		// from freshly created AST
		setAltASTPointers(r, t);

		// update Rule to just one alt and add prec alt
		ActionAST arg = (ActionAST)r.ast.getFirstChildWithType(ANTLRParser.ARG_ACTION);
		if ( arg!=null ) {
			r.args = ScopeParser.parseTypedArgList(arg, arg.getText(), g);
			r.args.type = AttributeDict.DictType.ARG;
			r.args.ast = arg;
			arg.resolver = r.alt[1]; // todo: isn't this Rule or something?
		}

		// define labels on recursive rule refs we delete; they don't point to nodes of course
		// these are so $label in action translation works
		for (Pair<GrammarAST,String> pair : leftRecursiveRuleAnalyzer.leftRecursiveRuleRefLabels) {
			GrammarAST labelNode = pair.getFirst();
			GrammarAST labelOpNode = (GrammarAST)labelNode.getParent();
			GrammarAST elementNode = (GrammarAST)labelOpNode.getChild(1);
			LabelElementPair lp = new LabelElementPair(g, labelNode, elementNode, labelOpNode.getType());
			r.alt[1].labelDefs.map(labelNode.getText(), lp);
		}
		// copy to rule from walker
		r.leftRecursiveRuleRefLabels = leftRecursiveRuleAnalyzer.leftRecursiveRuleRefLabels;

		tool.log("grammar", "added: "+t.toStringTree());
	}

	public RuleAST parseArtificialRule(final Grammar g, String ruleText) {
		ANTLRLexer lexer = new ANTLRLexer(new ANTLRStringStream(ruleText));
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(lexer.getCharStream());
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		lexer.tokens = tokens;
		ToolANTLRParser p = new ToolANTLRParser(tokens, tool);
		p.setTreeAdaptor(adaptor);
		Token ruleStart = null;
		try {
			ParserRuleReturnScope r = p.rule();
			RuleAST tree = (RuleAST)r.getTree();
			ruleStart = (Token)r.getStart();
			GrammarTransformPipeline.setGrammarPtr(g, tree);
			GrammarTransformPipeline.augmentTokensWithOriginalPosition(g, tree);
			return tree;
		}
		catch (Exception e) {
			tool.errMgr.toolError(ErrorType.INTERNAL_ERROR,
				e,
				ruleStart,
				"error parsing rule created during left-recursion detection: "+ruleText);
		}
		return null;
	}

	/**
	 * <pre>
	 * (RULE e int _p (returns int v)
	 * 	(BLOCK
	 * 	  (ALT
	 * 		(BLOCK
	 * 			(ALT INT {$v = $INT.int;})
	 * 			(ALT '(' (= x e) ')' {$v = $x.v;})
	 * 			(ALT ID))
	 * 		(* (BLOCK
	 *			(OPTIONS ...)
	 * 			(ALT {7 &gt;= $_p}? '*' (= b e) {$v = $a.v * $b.v;})
	 * 			(ALT {6 &gt;= $_p}? '+' (= b e) {$v = $a.v + $b.v;})
	 * 			(ALT {3 &gt;= $_p}? '++') (ALT {2 &gt;= $_p}? '--'))))))
	 * </pre>
	 */
	public void setAltASTPointers(LeftRecursiveRule r, RuleAST t) {
//		System.out.println("RULE: "+t.toStringTree());
		BlockAST ruleBlk = (BlockAST)t.getFirstChildWithType(ANTLRParser.BLOCK);
		AltAST mainAlt = (AltAST)ruleBlk.getChild(0);
		BlockAST primaryBlk = (BlockAST)mainAlt.getChild(0);
		BlockAST opsBlk = (BlockAST)mainAlt.getChild(1).getChild(0); // (* BLOCK ...)
		for (int i = 0; i < r.recPrimaryAlts.size(); i++) {
			LeftRecursiveRuleAltInfo altInfo = r.recPrimaryAlts.get(i);
			altInfo.altAST = (AltAST)primaryBlk.getChild(i);
			altInfo.altAST.leftRecursiveAltInfo = altInfo;
			altInfo.originalAltAST.leftRecursiveAltInfo = altInfo;
//			altInfo.originalAltAST.parent = altInfo.altAST.parent;
//			System.out.println(altInfo.altAST.toStringTree());
		}
		for (int i = 0; i < r.recOpAlts.size(); i++) {
			LeftRecursiveRuleAltInfo altInfo = r.recOpAlts.getElement(i);
			altInfo.altAST = (AltAST)opsBlk.getChild(i);
			altInfo.altAST.leftRecursiveAltInfo = altInfo;
			altInfo.originalAltAST.leftRecursiveAltInfo = altInfo;
//			altInfo.originalAltAST.parent = altInfo.altAST.parent;
//			System.out.println(altInfo.altAST.toStringTree());
		}
	}

}
