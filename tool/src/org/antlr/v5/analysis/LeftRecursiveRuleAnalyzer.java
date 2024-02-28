/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.analysis;

import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.v5.Tool;
import org.antlr.v5.codegen.CodeGenerator;
import org.antlr.v5.runtime.misc.IntervalSet;
import org.antlr.v5.runtime.misc.Pair;
import org.antlr.v5.tool.LeftRecursiveRule;
import org.antlr.v5.tool.ast.AltAST;
import org.antlr.v5.tool.ast.GrammarAST;
import org.antlr.v5.tool.ast.RuleRefAST;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.FileNotFoundException;
import java.util.*;

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
	public List<Pair<GrammarAST, String>> leftRecursiveRuleRefLabels = new ArrayList<>();

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

	public void analyze() {
		List<RuleAltInfo> ruleAltInfos = rule.ruleInfo.ruleAltInfos;
		for (RuleAltInfo ruleAltInfo : ruleAltInfos) {
			processAlternative(ruleAltInfo);
		}
	}

	private void processAlternative(RuleAltInfo ruleAltInfo) {
		int altNumber = ruleAltInfo.number;
		String label = null;
		boolean isListLabel = false;

		AltType altType = ruleAltInfo.type;
		if (altType == AltType.binaryLR || altType == AltType.suffixLR) {
			GrammarAST leftRecursiveRuleRefLabel = ruleAltInfo.leftRecursiveRuleRefLabel;
			if (leftRecursiveRuleRefLabel != null) {
				label = leftRecursiveRuleRefLabel.getText();
				isListLabel = leftRecursiveRuleRefLabel.getParent().getType() == PLUS_ASSIGN;
				leftRecursiveRuleRefLabels.add(new Pair<>(leftRecursiveRuleRefLabel, ruleAltInfo.label));
			}
		}

		int nextPrec = -1;
		boolean isBinaryOrPrefix = altType == AltType.binaryLR || altType == AltType.prefix;
		if (isBinaryOrPrefix) {
			// rewrite e to be e_[rec_arg]
			nextPrec = altType == AltType.binaryLR ? nextPrecedence(altNumber, ruleAltInfo.assocType) : precedence(altNumber);
		}

		String altText = text(ruleAltInfo, nextPrec).trim();
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

	private int nextPrecedence(int altNumber, AssocType assocType) {
		int p = precedence(altNumber);
		return assocType == AssocType.right ? p : p + 1;
	}

	private int precedence(int altNumber) {
		return rule.ruleInfo.ruleAltInfos.size() - altNumber + 1;
	}

	private String text(RuleAltInfo ruleAltInfo, int nextPrecedence) {
		AltAST altAST = ruleAltInfo.ast;
		if (altAST==null) return "";

		IntervalSet ignoredTokenIndexes = new IntervalSet();
		IntervalSet ignoredOptionTokenIndexes = new IntervalSet();

		List<GrammarAST> optionsSubTrees = altAST.getNodesWithType(ELEMENT_OPTIONS);
		for (GrammarAST optionSubTree : optionsSubTrees) {
			Tree child = optionSubTree.getChild(0);
			Tree optionNameAst = child.getChild(0);
			if (optionNameAst != null) {
				if (optionNameAst.getText().equals("assoc")) {
					ignoredTokenIndexes.add(optionSubTree.getTokenStartIndex(), optionSubTree.getTokenStopIndex());
				} else {
					ignoredOptionTokenIndexes.add(child.getTokenStartIndex(), child.getTokenStopIndex());
				}
			}
		}

		for (RuleRefAST ruleRefAST : ruleAltInfo.leftRecursiveLeftmostRuleRefs) {
			Tree parent = ruleRefAST.getParent();
			int startIgnoreIndex = ruleRefAST.getTokenStartIndex();
			if (parent instanceof GrammarAST) {
				int parenTokenType = ((GrammarAST) parent).token.getType();
				if (parenTokenType == ASSIGN || parenTokenType == PLUS_ASSIGN) {
					startIgnoreIndex = parent.getChild(0).getTokenStartIndex();
				}
			}
			ignoredTokenIndexes.add(startIgnoreIndex, ruleRefAST.getTokenStopIndex());
		}

		// Individual labels appear as RULE_REF or TOKEN_REF tokens in the tree,
		// but do not support the ELEMENT_OPTIONS syntax. Make sure to not try
		// and add the tokenIndex option when writing these tokens.
		List<GrammarAST> labeledSubTrees = altAST.getNodesWithType(new IntervalSet(ASSIGN,PLUS_ASSIGN));
		for (GrammarAST sub : labeledSubTrees) {
			ignoredOptionTokenIndexes.add(sub.getChild(0).getTokenStartIndex());
		}

		int tokenStartIndex = altAST.getTokenStartIndex();
		int tokenStopIndex = altAST.getTokenStopIndex();

		StringBuilder buffer = new StringBuilder();
		int tokenIndex = tokenStartIndex;

		while (tokenIndex <= tokenStopIndex) {
			if (ignoredTokenIndexes.contains(tokenIndex)) {
				tokenIndex++;
				continue;
			}

			Token token = tokenStream.get(tokenIndex);
			int tokenType = token.getType();
			if (tokenType == POUND) {
				break;
			}

			GrammarAST node = altAST.getNodeWithTokenIndex(token.getTokenIndex());
			buffer.append(token.getText()); // add actual text of the current token to the rewritten alternative
			boolean includeTokenOption = !ignoredOptionTokenIndexes.contains(tokenIndex);
			tokenIndex++;

			// Are there args on a rule?
			if (tokenType == RULE_REF && tokenIndex <= tokenStopIndex && tokenStream.get(tokenIndex).getType() == ARG_ACTION) {
				buffer.append('[').append(tokenStream.get(tokenIndex).getText()).append(']');
				tokenIndex++;
			}

			if (includeTokenOption && node != null && (tokenType == TOKEN_REF || tokenType == STRING_LITERAL || tokenType == RULE_REF)) {
				buffer.append("<tokenIndex=").append(token.getTokenIndex());
				RuleRefAST ruleRefNode = node instanceof RuleRefAST ? (RuleRefAST) node : null;
				if (ruleAltInfo.leftRecursiveRightmostRuleRefs.contains(ruleRefNode)) {
					buffer.append(',')
							.append(LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME)
							.append('=')
							.append(nextPrecedence);
				}
				buffer.append('>');
			}
		}

		return buffer.toString();
	}
}
