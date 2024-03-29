/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.automata;


import kotlin.Triple;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.v5.analysis.LeftRecursiveRuleTransformer;
import org.antlr.v5.automata.optimization.ATNOptimizer;
import org.antlr.v5.parse.ANTLRParser;
import org.antlr.v5.parse.ATNBuilder;
import org.antlr.v5.parse.GrammarASTAdaptor;
import org.antlr.v5.runtime.core.atn.ATN;
import org.antlr.v5.runtime.core.atn.ATNType;
import org.antlr.v5.runtime.core.atn.LL1Analyzer;
import org.antlr.v5.runtime.core.misc.IntervalSet;
import org.antlr.v5.runtime.core.state.*;
import org.antlr.v5.runtime.core.transition.*;
import org.antlr.v5.semantics.UseDefAnalyzer;
import org.antlr.v5.tool.ErrorManager;
import org.antlr.v5.tool.ErrorType;
import org.antlr.v5.tool.Grammar;
import org.antlr.v5.tool.LeftRecursiveRule;
import org.antlr.v5.tool.LexerGrammar;
import org.antlr.v5.tool.Rule;
import org.antlr.v5.tool.ast.ActionAST;
import org.antlr.v5.tool.ast.AltAST;
import org.antlr.v5.tool.ast.BlockAST;
import org.antlr.v5.tool.ast.GrammarAST;
import org.antlr.v5.tool.ast.GrammarASTWithOptions;
import org.antlr.v5.tool.ast.PredAST;
import org.antlr.v5.tool.ast.QuantifierAST;
import org.antlr.v5.tool.ast.TerminalAST;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** ATN construction routines triggered by ATNBuilder.g.
 *
 *  No side-effects. It builds an {@link ATN} object and returns it.
 */
public class ParserATNFactory implements ATNFactory {

	public final Grammar g;


	public final ATN atn;

	public Rule currentRule;

	public int currentOuterAlt;


	protected final List<Triple<Rule, ATNState, ATNState>> preventEpsilonClosureBlocks =
		new ArrayList<Triple<Rule, ATNState, ATNState>>();


	protected final List<Triple<Rule, ATNState, ATNState>> preventEpsilonOptionalBlocks =
		new ArrayList<Triple<Rule, ATNState, ATNState>>();

	public ParserATNFactory(Grammar g) {
		if (g == null) {
			throw new NullPointerException("g");
		}

		this.g = g;

		ATNType atnType = g instanceof LexerGrammar ? ATNType.LEXER : ATNType.PARSER;
		int maxTokenType = g.getMaxTokenType();
		this.atn = new ATN(atnType, maxTokenType);
	}


	@Override
	public ATN createATN() {
		_createATN(g.rules.values());
		assert atn.getMaxTokenType() == g.getMaxTokenType();
        addRuleFollowLinks();
		addEOFTransitionToStartRules();
		ATNOptimizer.optimize(g, atn);
		checkEpsilonClosure();

		optionalCheck:
		for (Triple<Rule, ATNState, ATNState> pair : preventEpsilonOptionalBlocks) {
			int bypassCount = 0;
			for (int i = 0; i < pair.getSecond().getNumberOfTransitions(); i++) {
				ATNState startState = pair.getSecond().transition(i).getTarget();
				if (startState == pair.getThird()) {
					bypassCount++;
					continue;
				}

				LL1Analyzer analyzer = new LL1Analyzer(atn);
				if (analyzer.LOOK(startState, pair.getThird(), null).contains(org.antlr.v5.runtime.core.Token.EPSILON)) {
					g.tool.errMgr.grammarError(ErrorType.EPSILON_OPTIONAL, g.fileName, ((GrammarAST)pair.getFirst().ast.getChild(0)).getToken(), pair.getFirst().name);
					continue optionalCheck;
				}
			}

			if (bypassCount != 1) {
				throw new UnsupportedOperationException("Expected optional block with exactly 1 bypass alternative.");
			}
		}

		return atn;
	}

	protected void checkEpsilonClosure() {
		for (Triple<Rule, ATNState, ATNState> pair : preventEpsilonClosureBlocks) {
			LL1Analyzer analyzer = new LL1Analyzer(atn);
			ATNState blkStart = pair.getSecond();
			ATNState blkStop = pair.getThird();
			IntervalSet lookahead = analyzer.LOOK(blkStart, blkStop, null);
			if ( lookahead.contains(org.antlr.v5.runtime.core.Token.EPSILON)) {
				ErrorType errorType = pair.getFirst() instanceof LeftRecursiveRule ? ErrorType.EPSILON_LR_FOLLOW : ErrorType.EPSILON_CLOSURE;
				g.tool.errMgr.grammarError(errorType, g.fileName, ((GrammarAST)pair.getFirst().ast.getChild(0)).getToken(), pair.getFirst().name);
			}
			if ( lookahead.contains(org.antlr.v5.runtime.core.Token.EOF)) {
				g.tool.errMgr.grammarError(ErrorType.EOF_CLOSURE, g.fileName, ((GrammarAST)pair.getFirst().ast.getChild(0)).getToken(), pair.getFirst().name);
			}
		}
	}

	protected void _createATN(Collection<Rule> rules) {
		createRuleStartAndStopATNStates();

		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		for (Rule r : rules) {
			// find rule's block
			GrammarAST blk = (GrammarAST)r.ast.getFirstChildWithType(ANTLRParser.BLOCK);
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
			ATNBuilder b = new ATNBuilder(nodes,this);
			try {
				setCurrentRuleName(r.name);
				Handle h = b.ruleBlock(null);
				rule(r.ast, r.name, h);
			}
			catch (RecognitionException re) {
				ErrorManager.fatalInternalError("bad grammar AST structure", re);
			}
		}
	}

	@Override
	public void setCurrentRuleName(String name) {
		this.currentRule = g.getRule(name);
	}

	@Override
	public void setCurrentOuterAlt(int alt) {
		currentOuterAlt = alt;
	}

	/* start->ruleblock->end */

	@Override
	public Handle rule(GrammarAST ruleAST, String name, Handle blk) {
		Rule r = g.getRule(name);
		RuleStartState start = atn.getRuleToStartState()[r.index];
		epsilon(start, blk.left);
		RuleStopState stop = atn.getRuleToStopState()[r.index];
		epsilon(blk.right, stop);
		Handle h = new Handle(start, stop);
//		ATNPrinter ser = new ATNPrinter(g, h.left);
//		System.out.println(ruleAST.toStringTree()+":\n"+ser.asString());
		ruleAST.atnState = start;
		return h;
	}

	/** From label {@code A} build graph {@code o-A->o}. */

	@Override
	public Handle tokenRef(TerminalAST node) {
		ATNState left = newState(node);
		ATNState right = newState(node);
		int ttype = g.getTokenType(node.getText());
		left.addTransition(new AtomTransition(right, ttype));
		node.atnState = left;
		return new Handle(left, right);
	}

	/** From set build single edge graph {@code o->o-set->o}.  To conform to
     *  what an alt block looks like, must have extra state on left.
	 *  This also handles {@code ~A}, converted to {@code ~{A}} set.
     */

	@Override
	public Handle set(GrammarAST associatedAST, List<GrammarAST> terminals, boolean invert) {
		ATNState left = newState(associatedAST);
		ATNState right = newState(associatedAST);
		IntervalSet set = new IntervalSet();
		for (GrammarAST t : terminals) {
			int ttype = g.getTokenType(t.getText());
			set.add(ttype);
		}
		if ( invert ) {
			left.addTransition(new NotSetTransition(right, set));
		}
		else {
			left.addTransition(new SetTransition(right, set));
		}
		associatedAST.atnState = left;
		return new Handle(left, right);
	}

	/** Not valid for non-lexers. */

	@Override
	public Handle range(GrammarAST a, GrammarAST b) {
		g.tool.errMgr.grammarError(ErrorType.TOKEN_RANGE_IN_PARSER, g.fileName,
		                           a.getToken(),
		                           a.getToken().getText(),
		                           b.getToken().getText());
		// From a..b, yield ATN for just a.
		return tokenRef((TerminalAST)a);
	}

	/** For a non-lexer, just build a simple token reference atom. */

	@Override
	public Handle stringLiteral(TerminalAST stringLiteralAST) {
		return tokenRef(stringLiteralAST);
	}

	/** {@code [Aa]} char sets not allowed in parser */

	@Override
	public Handle charSetLiteral(GrammarAST charSetAST) {
		return null;
	}

	/**
	 * For reference to rule {@code r}, build
	 *
	 * <pre>
	 *  o-&gt;(r)  o
	 * </pre>
	 *
	 * where {@code (r)} is the start of rule {@code r} and the trailing
	 * {@code o} is not linked to from rule ref state directly (uses
	 * {@link RuleTransition#getFollowState}).
	 */

	@Override
	public Handle ruleRef(GrammarAST node) {
		Handle h = _ruleRef(node);
		return h;
	}


	public Handle _ruleRef(GrammarAST node) {
		Rule r = g.getRule(node.getText());
		if ( r==null ) {
			g.tool.errMgr.grammarError(ErrorType.INTERNAL_ERROR, g.fileName, node.getToken(), "Rule "+node.getText()+" undefined");
			return null;
		}
		RuleStartState start = atn.getRuleToStartState()[r.index];
		ATNState left = newState(node);
		ATNState right = newState(node);
		int precedence = 0;
		if (((GrammarASTWithOptions)node).getOptionString(LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME) != null) {
			precedence = Integer.parseInt(((GrammarASTWithOptions)node).getOptionString(LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME));
		}
		RuleTransition call = new RuleTransition(start, r.index, precedence, right);
		left.addTransition(call);

		node.atnState = left;
		return new Handle(left, right);
	}

	public void addFollowLink(int ruleIndex, ATNState right) {
		// add follow edge from end of invoked rule
		RuleStopState stop = atn.getRuleToStopState()[ruleIndex];
//        System.out.println("add follow link from "+ruleIndex+" to "+right);
		epsilon(stop, right);
	}

	/** From an empty alternative build {@code o-e->o}. */

	@Override
	public Handle epsilon(GrammarAST node) {
		ATNState left = newState(node);
		ATNState right = newState(node);
		epsilon(left, right);
		node.atnState = left;
		return new Handle(left, right);
	}

	/** Build what amounts to an epsilon transition with a semantic
	 *  predicate action.  The {@code pred} is a pointer into the AST of
	 *  the {@link ANTLRParser#SEMPRED} token.
	 */

	@Override
	public Handle sempred(PredAST pred) {
		//System.out.println("sempred: "+ pred);
		ATNState left = newState(pred);
		ATNState right = newState(pred);

		AbstractPredicateTransition p;
		if (pred.getOptionString(LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME) != null) {
			int precedence = Integer.parseInt(pred.getOptionString(LeftRecursiveRuleTransformer.PRECEDENCE_OPTION_NAME));
			p = new PrecedencePredicateTransition(right, precedence);
		}
		else {
			boolean isCtxDependent = UseDefAnalyzer.actionIsContextDependent(pred);
			p = new PredicateTransition(right, currentRule.index, g.sempreds.get(pred), isCtxDependent);
		}

		left.addTransition(p);
		pred.atnState = left;
		return new Handle(left, right);
	}

	/** Build what amounts to an epsilon transition with an action.
	 *  The action goes into ATN though it is ignored during prediction
	 *  if {@link ActionTransition#getActionIndex actionIndex}{@code <0}.
	 */

	@Override
	public Handle action(ActionAST action) {
		//System.out.println("action: "+action);
		ATNState left = newState(action);
		ATNState right = newState(action);
		ActionTransition a = new ActionTransition(right, currentRule.index, -1, false);
		left.addTransition(a);
		action.atnState = left;
		return new Handle(left, right);
	}


	@Override
	public Handle action(String action) {
		throw new UnsupportedOperationException("This element is not valid in parsers.");
	}

	/**
	 * From {@code A|B|..|Z} alternative block build
	 *
	 * <pre>
	 *  o-&gt;o-A-&gt;o-&gt;o (last ATNState is BlockEndState pointed to by all alts)
	 *  |          ^
	 *  |-&gt;o-B-&gt;o--|
	 *  |          |
	 *  ...        |
	 *  |          |
	 *  |-&gt;o-Z-&gt;o--|
	 * </pre>
	 *
	 * So start node points at every alternative with epsilon transition and
	 * every alt right side points at a block end ATNState.
	 * <p>
	 * Special case: only one alternative: don't make a block with alt
	 * begin/end.
	 * <p>
	 * Special case: if just a list of tokens/chars/sets, then collapse to a
	 * single edged o-set-&gt;o graph.
	 * <p>
	 * TODO: Set alt number (1..n) in the states?
	 */

	@Override
	public Handle block(BlockAST blkAST, GrammarAST ebnfRoot, List<Handle> alts) {
		if ( ebnfRoot==null ) {
			if ( alts.size()==1 ) {
				Handle h = alts.get(0);
				blkAST.atnState = h.left;
				return h;
			}
			BlockStartState start = newState(BasicBlockStartState.class, blkAST);
			if ( alts.size()>1 ) atn.defineDecisionState(start);
			return makeBlock(start, blkAST, alts);
		}
		switch ( ebnfRoot.getType() ) {
			case ANTLRParser.OPTIONAL :
				BlockStartState start = newState(BasicBlockStartState.class, blkAST);
				atn.defineDecisionState(start);
				Handle h = makeBlock(start, blkAST, alts);
				return optional(ebnfRoot, h);
			case ANTLRParser.CLOSURE :
				BlockStartState star = newState(StarBlockStartState.class, ebnfRoot);
				if ( alts.size()>1 ) atn.defineDecisionState(star);
				h = makeBlock(star, blkAST, alts);
				return star(ebnfRoot, h);
			case ANTLRParser.POSITIVE_CLOSURE :
				PlusBlockStartState plus = newState(PlusBlockStartState.class, ebnfRoot);
				if ( alts.size()>1 ) atn.defineDecisionState(plus);
				h = makeBlock(plus, blkAST, alts);
				return plus(ebnfRoot, h);
		}
		return null;
	}


	protected Handle makeBlock(BlockStartState start, BlockAST blkAST, List<Handle> alts) {
		BlockEndState end = newState(BlockEndState.class, blkAST);
		start.setEndState(end);
		for (Handle alt : alts) {
			// hook alts up to decision block
			epsilon(start, alt.left);
			epsilon(alt.right, end);
		}
		Handle h = new Handle(start, end);
//		FASerializer ser = new FASerializer(g, h.left);
//		System.out.println(blkAST.toStringTree()+":\n"+ser);
		blkAST.atnState = start;

		return h;
	}


	@Override
	public Handle alt(List<Handle> els) {
		return elemList(els);
	}


	public Handle elemList(List<Handle> els) {
		int n = els.size();
		Handle first = els.get(0);
		Handle nextEl = first;
		for (int i = 1; i < n; i++) {
			Handle el = nextEl;
			nextEl = els.get(i);
			if (el != null && nextEl != null) {
				epsilon(el.right, nextEl.left);
			}
		}
		ATNState left = null;
		if (first != null) {
			left = first.left;
		}
		ATNState right = null;
		if (nextEl != null) {
			right = nextEl.right;
		}
		return new Handle(left, right);
	}

	/**
	 * From {@code (A)?} build either:
	 *
	 * <pre>
	 *  o--A-&gt;o
	 *  |     ^
	 *  o----&gt;|
	 * </pre>
	 *
	 * or, if {@code A} is a block, just add an empty alt to the end of the
	 * block
	 */

	@Override
	public Handle optional(GrammarAST optAST, Handle blk) {
		BlockStartState blkStart = (BlockStartState)blk.left;
		ATNState blkEnd = blk.right;
		preventEpsilonOptionalBlocks.add(new Triple<Rule, ATNState, ATNState>(currentRule, blkStart, blkEnd));

		boolean greedy = ((QuantifierAST)optAST).isGreedy();
		blkStart.setNonGreedy(!greedy);
		epsilon(blkStart, blk.right, !greedy);

		optAST.atnState = blk.left;
		return blk;
	}

	/**
	 * From {@code (blk)+} build
	 *
	 * <pre>
	 *   |---------|
	 *   v         |
	 *  [o-blk-o]-&gt;o-&gt;o
	 * </pre>
	 *
	 * We add a decision for loop back node to the existing one at {@code blk}
	 * start.
	 */

	@Override
	public Handle plus(GrammarAST plusAST, Handle blk) {
		PlusBlockStartState blkStart = (PlusBlockStartState)blk.left;
		BlockEndState blkEnd = (BlockEndState)blk.right;
		preventEpsilonClosureBlocks.add(new Triple<Rule, ATNState, ATNState>(currentRule, blkStart, blkEnd));

		PlusLoopbackState loop = newState(PlusLoopbackState.class, plusAST);
		loop.setNonGreedy(!((QuantifierAST) plusAST).isGreedy());
		atn.defineDecisionState(loop);
		LoopEndState end = newState(LoopEndState.class, plusAST);
		blkStart.setLoopBackState(loop);
		end.setLoopBackState(loop);

		plusAST.atnState = loop;
		epsilon(blkEnd, loop);		// blk can see loop back

		BlockAST blkAST = (BlockAST)plusAST.getChild(0);
		if ( ((QuantifierAST)plusAST).isGreedy() ) {
			if (expectNonGreedy(blkAST)) {
				g.tool.errMgr.grammarError(ErrorType.EXPECTED_NON_GREEDY_WILDCARD_BLOCK, g.fileName, plusAST.getToken(), plusAST.getToken().getText());
			}

			epsilon(loop, blkStart);	// loop back to start
			epsilon(loop, end);			// or exit
		}
		else {
			// if not greedy, priority to exit branch; make it first
			epsilon(loop, end);			// exit
			epsilon(loop, blkStart);	// loop back to start
		}

		return new Handle(blkStart, end);
	}

	/**
	 * From {@code (blk)*} build {@code ( blk+ )?} with *two* decisions, one for
	 * entry and one for choosing alts of {@code blk}.
	 *
	 * <pre>
	 *   |-------------|
	 *   v             |
	 *   o--[o-blk-o]-&gt;o  o
	 *   |                ^
	 *   -----------------|
	 * </pre>
	 *
	 * Note that the optional bypass must jump outside the loop as
	 * {@code (A|B)*} is not the same thing as {@code (A|B|)+}.
	 */

	@Override
	public Handle star(GrammarAST starAST, Handle elem) {
		StarBlockStartState blkStart = (StarBlockStartState)elem.left;
		BlockEndState blkEnd = (BlockEndState)elem.right;
		preventEpsilonClosureBlocks.add(new Triple<Rule, ATNState, ATNState>(currentRule, blkStart, blkEnd));

		StarLoopEntryState entry = newState(StarLoopEntryState.class, starAST);
		entry.setNonGreedy(!((QuantifierAST) starAST).isGreedy());
		atn.defineDecisionState(entry);
		LoopEndState end = newState(LoopEndState.class, starAST);
		StarLoopbackState loop = newState(StarLoopbackState.class, starAST);
		entry.setLoopBackState(loop);
		end.setLoopBackState(loop);

		BlockAST blkAST = (BlockAST)starAST.getChild(0);
		if ( ((QuantifierAST)starAST).isGreedy() ) {
			if (expectNonGreedy(blkAST)) {
				g.tool.errMgr.grammarError(ErrorType.EXPECTED_NON_GREEDY_WILDCARD_BLOCK, g.fileName, starAST.getToken(), starAST.getToken().getText());
			}

			epsilon(entry, blkStart);	// loop enter edge (alt 1)
			epsilon(entry, end);		// bypass loop edge (alt 2)
		}
		else {
			// if not greedy, priority to exit branch; make it first
			epsilon(entry, end);		// bypass loop edge (alt 1)
			epsilon(entry, blkStart);	// loop enter edge (alt 2)
		}
		epsilon(blkEnd, loop);		// block end hits loop back
		epsilon(loop, entry);		// loop back to entry/exit decision

		starAST.atnState = entry;	// decision is to enter/exit; blk is its own decision
		return new Handle(entry, end);
	}

	/** Build an atom with all possible values in its label. */

	@Override
	public Handle wildcard(GrammarAST node) {
		ATNState left = newState(node);
		ATNState right = newState(node);
		left.addTransition(new WildcardTransition(right));
		node.atnState = left;
		return new Handle(left, right);
	}

	protected void epsilon(ATNState a, ATNState b) {
		epsilon(a, b, false);
	}

	protected void epsilon(ATNState a, ATNState b, boolean prepend) {
		if ( a!=null ) {
			int index = prepend ? 0 : a.getNumberOfTransitions();
			a.addTransition(index, new EpsilonTransition(b,  -1));
		}
	}

	/** Define all the rule begin/end ATNStates to solve forward reference
	 *  issues.
	 */
	void createRuleStartAndStopATNStates() {
		atn.setRuleToStartState(new RuleStartState[g.rules.size()]);
		atn.setRuleToStopState(new RuleStopState[g.rules.size()]);
		for (Rule r : g.rules.values()) {
			RuleStartState start = newState(RuleStartState.class, r.ast);
			RuleStopState stop = newState(RuleStopState.class, r.ast);
			start.setStopState(stop);
			start.setLeftRecursiveRule(r instanceof LeftRecursiveRule);
			start.setRuleIndex(r.index);
			stop.setRuleIndex(r.index);
			atn.getRuleToStartState()[r.index] = start;
			atn.getRuleToStopState()[r.index] = stop;
		}
	}

    public void addRuleFollowLinks() {
        for (ATNState p : atn.getStates()) {
            if ( p!=null &&
                 p.getStateType() == ATNState.BASIC && p.getNumberOfTransitions()==1 &&
                 p.transition(0) instanceof RuleTransition )
            {
                RuleTransition rt = (RuleTransition) p.transition(0);
                addFollowLink(rt.getRuleIndex(), rt.getFollowState());
            }
        }
    }

	/** Add an EOF transition to any rule end ATNState that points to nothing
     *  (i.e., for all those rules not invoked by another rule).  These
     *  are start symbols then.
	 *
	 *  Return the number of grammar entry points; i.e., how many rules are
	 *  not invoked by another rule (they can only be invoked from outside).
	 *  These are the start rules.
     */
	public int addEOFTransitionToStartRules() {
		int n = 0;
		ATNState eofTarget = newState(null); // one unique EOF target for all rules
		for (Rule r : g.rules.values()) {
			ATNState stop = atn.getRuleToStopState()[r.index];
			if ( stop.getNumberOfTransitions()>0 ) continue;
			n++;
			Transition t = new AtomTransition(eofTarget, Token.EOF);
			stop.addTransition(t);
		}
		return n;
	}


	@Override
	public Handle label(Handle t) {
		return t;
	}


	@Override
	public Handle listLabel(Handle t) {
		return t;
	}


	public <T extends ATNState> T newState(Class<T> nodeType, GrammarAST node) {
		Exception cause;
		try {
			Constructor<T> ctor = nodeType.getConstructor();
			T s = ctor.newInstance();
			if ( currentRule==null ) s.setRuleIndex(-1);
			else s.setRuleIndex(currentRule.index);
			atn.addState(s);
			return s;
		} catch (InstantiationException ex) {
			cause = ex;
		} catch (IllegalAccessException ex) {
			cause = ex;
		} catch (IllegalArgumentException ex) {
			cause = ex;
		} catch (InvocationTargetException ex) {
			cause = ex;
		} catch (NoSuchMethodException ex) {
			cause = ex;
		} catch (SecurityException ex) {
			cause = ex;
		}

		String message = String.format("Could not create %s of type %s.", ATNState.class.getName(), nodeType.getName());
		throw new UnsupportedOperationException(message, cause);
	}


	public ATNState newState(GrammarAST node) {
		ATNState n = new BasicState();
		n.setRuleIndex(currentRule.index);
		atn.addState(n);
		return n;
	}


	@Override
	public ATNState newState() { return newState(null); }

	public boolean expectNonGreedy(BlockAST blkAST) {
		if ( blockHasWildcardAlt(blkAST) ) {
			return true;
		}

		return false;
	}

	/**
	 * {@code (BLOCK (ALT .))} or {@code (BLOCK (ALT 'a') (ALT .))}.
	 */
	public static boolean blockHasWildcardAlt(GrammarAST block) {
		for (Object alt : block.getChildren()) {
			if ( !(alt instanceof AltAST) ) continue;
			AltAST altAST = (AltAST)alt;
			if ( altAST.getChildCount()==1 || (altAST.getChildCount() == 2 && altAST.getChild(0).getType() == ANTLRParser.ELEMENT_OPTIONS) ) {
				Tree e = altAST.getChild(altAST.getChildCount() - 1);
				if ( e.getType()==ANTLRParser.WILDCARD ) {
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public Handle lexerAltCommands(Handle alt, Handle cmds) {
		throw new UnsupportedOperationException("This element is not allowed in parsers.");
	}


	@Override
	public Handle lexerCallCommand(GrammarAST ID, GrammarAST arg) {
		throw new UnsupportedOperationException("This element is not allowed in parsers.");
	}


	@Override
	public Handle lexerCommand(GrammarAST ID) {
		throw new UnsupportedOperationException("This element is not allowed in parsers.");
	}
}
