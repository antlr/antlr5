/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


package org.antlr.v5.analysis;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.v5.Tool;
import org.antlr.v5.codegen.CodeGenerator;
import org.antlr.v5.parse.ANTLRParser;
import org.antlr.v5.runtime.misc.IntervalSet;
import org.antlr.v5.runtime.misc.Pair;
import org.antlr.v5.tool.LeftRecursiveRule;
import org.antlr.v5.tool.ast.AltAST;
import org.antlr.v5.tool.ast.GrammarAST;
import org.antlr.v5.tool.ast.GrammarASTWithOptions;
import org.antlr.v5.tool.ast.RuleRefAST;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.antlr.v5.parse.ANTLRLexer.PLUS_ASSIGN;
import static org.antlr.v5.parse.ANTLRLexer.POUND;
import static org.antlr.v5.parse.ANTLRParser.*;

public class LeftRecursiveRuleAnalyzer {
	public Tool tool;
	public LeftRecursiveRule rule;

	public LinkedHashMap<Integer, LeftRecursiveRuleAltInfo> binaryAlts = new LinkedHashMap<>();
	public LinkedHashMap<Integer, LeftRecursiveRuleAltInfo> suffixAlts = new LinkedHashMap<>();
	public List<LeftRecursiveRuleAltInfo> prefixAndOtherAlts = new ArrayList<>();

	public final TokenStream tokenStream;

	/** Pointer to ID node of ^(= ID element) */
	public List<Pair<GrammarAST,String>> leftRecursiveRuleRefLabels =
			new ArrayList<>();

	public final static STGroup recRuleTemplates;
	public final STGroup codegenTemplates;
	public final String language;

	static {
		String templateGroupFile = "org/antlr/v5/tool/templates/LeftRecursiveRules.stg";
		recRuleTemplates = new STGroupFile(templateGroupFile);
		if (!recRuleTemplates.isDefined("recRule")) {
			try {
				throw new FileNotFoundException("can't find code generation templates: LeftRecursiveRules");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public LeftRecursiveRuleAnalyzer(Tool tool, LeftRecursiveRule rule, TokenStream tokenStream, String language) {
		this.tool = tool;
		this.rule = rule;
		this.language = language;
		this.tokenStream = tokenStream;
		if (this.tokenStream == null) {
			throw new NullPointerException("grammar must have a token stream");
		}

		// use codegen to get correct language templates; that's it though
		codegenTemplates = CodeGenerator.create(tool, null, language).getTemplates();
	}

	public void analyze() {
		List<RuleAltInfo> ruleAltInfos = rule.ruleInfo.ruleAltInfos;
		for (RuleAltInfo ruleAltInfo : ruleAltInfos) {
			processAlternative(ruleAltInfo);
		}
	}

	private void processAlternative(RuleAltInfo ruleAltInfo) {
		AltAST altTree = (AltAST) ruleAltInfo.ast.dupTree();
		int altNumber = ruleAltInfo.number;
		stripAltLabel(altTree);
		String label = null;
		boolean isListLabel = false;

		AltType altType = ruleAltInfo.type;
		if (altType == AltType.binaryLR || altType == AltType.suffixLR) {
			GrammarAST lrlabel = stripLeftRecursion(altTree);
			if (lrlabel != null) {
				label = lrlabel.getText();
				isListLabel = lrlabel.getParent().getType() == PLUS_ASSIGN;
				leftRecursiveRuleRefLabels.add(new Pair<>(lrlabel, ruleAltInfo.label));
			}
		}

		int nextPrec = -1;
		boolean isBinaryOrPrefix = altType == AltType.binaryLR || altType == AltType.prefix;
		if (isBinaryOrPrefix) {
			// rewrite e to be e_[rec_arg]
			nextPrec = altType == AltType.binaryLR ? nextPrecedence(altNumber, ruleAltInfo.assocType) : precedence(altNumber);
			altTree = addPrecedenceArgToRules(altTree, nextPrec);
		}

		String altText = text(altTree).trim();
		LeftRecursiveRuleAltInfo lrInfo = new LeftRecursiveRuleAltInfo(altNumber, altText, label, ruleAltInfo.label, isListLabel, ruleAltInfo.ast);

		if (isBinaryOrPrefix) {
			lrInfo.nextPrec = nextPrec;
		}

		if (altType == AltType.binaryLR) {
			binaryAlts.put(altNumber, lrInfo);
		} else if (altType == AltType.suffixLR) {
			suffixAlts.put(altNumber, lrInfo);
		} else {
			prefixAndOtherAlts.add(lrInfo);
		}
	}

	/** Strip last 2 tokens if → label; alter indexes in altAST */
	private void stripAltLabel(GrammarAST altAST) {
		int start = altAST.getTokenStartIndex();
		int stop = altAST.getTokenStopIndex();
		// find =>
		for (int i=stop; i>=start; i--) {
			if ( tokenStream.get(i).getType()==POUND ) {
				altAST.setTokenStopIndex(i-1);
				return;
			}
		}
	}

	// TODO: this strips the tree properly, but since text()
	// uses the start of stop token index and gets text from that
	// ineffectively ignores this routine.
	private GrammarAST stripLeftRecursion(Tree tree) {
		GrammarAST lrLabel = null;
		switch (tree.getType()) {
			case ALT:
				return stripLeftRecursion(tree.getChild(0));
			case BLOCK:
				for (int i = 0; i < tree.getChildCount(); i++) {
					GrammarAST temp = stripLeftRecursion(tree.getChild(i));
					// TODO: error about conflicting labels
					if (i == 0) {
						lrLabel = temp;
					}
				}
				return lrLabel;
			case ELEMENT_OPTIONS:
				return stripLeftRecursion(tree.getParent().getChild(1));
			case ASSIGN:
			case PLUS_ASSIGN:
				lrLabel = (GrammarAST)tree.getChild(0);
				stripLeftRecursion(tree.getChild(1));
				return lrLabel;
			case RULE_REF:
				Tree parent = tree.getParent();
				if (parent.getType() == ASSIGN || parent.getType() == PLUS_ASSIGN) {
					parent = parent.getParent();
				}
				int index = parent.getType() == ALT && parent.getChild(0).getType() == ELEMENT_OPTIONS ? 1 : 0;
				// remove rule ref (first child unless options present)
				parent.deleteChild(index);
				// reset index so it prints properly (sets token range of
				// ALT to start to right of left recur rule we deleted)
				GrammarAST newFirstChild = (GrammarAST)parent.getChild(index);
				if (newFirstChild != null) {
					parent.setTokenStartIndex(newFirstChild.getTokenStartIndex());
				}
				break;
		}
		return null;
	}

	private int precedence(int altNumber) {
		return rule.ruleInfo.ruleAltInfos.size()-altNumber+1;
	}

	// Assumes left assoc
	private int nextPrecedence(int altNumber, AssocType assocType) {
		int p = precedence(altNumber);
		if (assocType == AssocType.right) return p;
		return p+1;
	}

	// --------- get transformed rules ----------------

	public String getArtificialOpPrecRule() {
		ST ruleST = recRuleTemplates.getInstanceOf("recRule");
		ruleST.add("ruleName", rule.name);
		ST ruleArgST = codegenTemplates.getInstanceOf("recRuleArg");
		ruleST.add("argName", ruleArgST);
		ST setResultST = codegenTemplates.getInstanceOf("recRuleSetResultAction");
		ruleST.add("setResultAction", setResultST);
		ruleST.add("userRetvals", rule.ruleInfo.retvals);

		LinkedHashMap<Integer, LeftRecursiveRuleAltInfo> opPrecRuleAlts = new LinkedHashMap<>();
		opPrecRuleAlts.putAll(binaryAlts);
		opPrecRuleAlts.putAll(suffixAlts);
		for (int alt : opPrecRuleAlts.keySet()) {
			LeftRecursiveRuleAltInfo altInfo = opPrecRuleAlts.get(alt);
			ST altST = recRuleTemplates.getInstanceOf("recRuleAlt");
			ST predST = codegenTemplates.getInstanceOf("recRuleAltPredicate");
			predST.add("opPrec", precedence(alt));
			predST.add("ruleName", rule.name);
			altST.add("pred", predST);
			altST.add("alt", altInfo);
			altST.add("precOption", LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME);
			altST.add("opPrec", precedence(alt));
			ruleST.add("opAlts", altST);
		}

		ruleST.add("primaryAlts", prefixAndOtherAlts);

		String renderResult = ruleST.render();
		tool.log("left-recursion", renderResult);
		return renderResult;
	}

	private AltAST addPrecedenceArgToRules(AltAST t, int prec) {
		if ( t==null ) return null;
		// get all top-level rule refs from ALT
		List<GrammarAST> outerAltRuleRefs = t.getNodesWithTypePreorderDFS(IntervalSet.of(RULE_REF));
		for (GrammarAST x : outerAltRuleRefs) {
			RuleRefAST rref = (RuleRefAST)x;
			boolean recursive = rref.getText().equals(rule.name);
			boolean rightmost = rref == outerAltRuleRefs.get(outerAltRuleRefs.size()-1);
			if ( recursive && rightmost ) {
				GrammarAST dummyValueNode = new GrammarAST(new CommonToken(ANTLRParser.INT, String.valueOf(prec)));
				rref.setOption(LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME, dummyValueNode);
			}
		}
		return t;
	}

	private String text(GrammarAST t) {
		if ( t==null ) return "";

		int tokenStartIndex = t.getTokenStartIndex();
		int tokenStopIndex = t.getTokenStopIndex();

		// ignore tokens from existing option subtrees like:
		//    (ELEMENT_OPTIONS (= assoc right))
		//
		// element options are added back according to the values in the map
		// returned by getOptions().
		IntervalSet ignore = new IntervalSet();
		List<GrammarAST> optionsSubTrees = t.getNodesWithType(ELEMENT_OPTIONS);
		for (GrammarAST sub : optionsSubTrees) {
			ignore.add(sub.getTokenStartIndex(), sub.getTokenStopIndex());
		}

		// Individual labels appear as RULE_REF or TOKEN_REF tokens in the tree,
		// but do not support the ELEMENT_OPTIONS syntax. Make sure to not try
		// and add the tokenIndex option when writing these tokens.
		IntervalSet noOptions = new IntervalSet();
		List<GrammarAST> labeledSubTrees = t.getNodesWithType(new IntervalSet(ASSIGN,PLUS_ASSIGN));
		for (GrammarAST sub : labeledSubTrees) {
			noOptions.add(sub.getChild(0).getTokenStartIndex());
		}

		StringBuilder buf = new StringBuilder();
		int i=tokenStartIndex;
		while ( i<=tokenStopIndex ) {
			if ( ignore.contains(i) ) {
				i++;
				continue;
			}

			Token tok = tokenStream.get(i);

			// Compute/hold any element options
			StringBuilder elementOptions = new StringBuilder();
			if (!noOptions.contains(i)) {
				GrammarAST node = t.getNodeWithTokenIndex(tok.getTokenIndex());
				if ( node!=null &&
						(tok.getType()==TOKEN_REF ||
								tok.getType()==STRING_LITERAL ||
								tok.getType()==RULE_REF) )
				{
					elementOptions.append("tokenIndex=").append(tok.getTokenIndex());
				}

				if ( node instanceof GrammarASTWithOptions) {
					GrammarASTWithOptions o = (GrammarASTWithOptions)node;
					for (Map.Entry<String, GrammarAST> entry : o.getOptions().entrySet()) {
						if (elementOptions.length() > 0) {
							elementOptions.append(',');
						}

						elementOptions.append(entry.getKey());
						elementOptions.append('=');
						elementOptions.append(entry.getValue().getText());
					}
				}
			}

			buf.append(tok.getText()); // add actual text of the current token to the rewritten alternative
			i++;                       // move to the next token

			// Are there args on a rule?
			if ( tok.getType()==RULE_REF && i<=tokenStopIndex && tokenStream.get(i).getType()==ARG_ACTION ) {
				buf.append('[').append(tokenStream.get(i).getText()).append(']');
				i++;
			}

			// now that we have the actual element, we can add the options.
			if (elementOptions.length() > 0) {
				buf.append('<').append(elementOptions).append('>');
			}
		}
		return buf.toString();
	}
}
