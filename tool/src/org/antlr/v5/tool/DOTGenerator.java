/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.tool;

import org.antlr.v5.misc.Utils;
import org.antlr.v5.runtime.core.atn.ATNConfig;
import org.antlr.v5.runtime.core.dfa.DFA;
import org.antlr.v5.runtime.core.dfa.DFAState;
import org.antlr.v5.runtime.core.misc.IntegerList;
import org.antlr.v5.runtime.core.state.*;
import org.antlr.v5.runtime.core.transition.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/** The DOT (part of graphviz) generation aspect. */
public class DOTGenerator {
	public static final boolean STRIP_NONREDUCED_STATES = false;

	protected String arrowhead="normal";
	protected String rankdir="LR";

	/** Library of output templates; use {@code <attrname>} format. */
    public final static STGroup stlib = new STGroupFile("org/antlr/v5/tool/templates/dot/graphs.stg");

    protected Grammar grammar;

    /** This aspect is associated with a grammar */
	public DOTGenerator(Grammar grammar) {
		this.grammar = grammar;
	}

	public String getDOT(DFA dfa, boolean isLexer) {
		if ( dfa.getS0() ==null )	return null;

		ST dot = stlib.getInstanceOf("dfa");
		dot.add("name", "DFA"+ dfa.getDecision());
		dot.add("startState", dfa.getS0().getStateNumber());
//		dot.add("useBox", Tool.internalOption_ShowATNConfigsInDFA);
		dot.add("rankdir", rankdir);

		// define stop states first; seems to be a bug in DOT where doublecircle
		for (DFAState d : dfa.getStatesMap().keySet()) {
			if ( !d.isAcceptState()) continue;
			ST st = stlib.getInstanceOf("stopstate");
			st.add("name", "s"+ d.getStateNumber());
			st.add("label", getStateLabel(d));
			dot.add("states", st);
		}

		for (DFAState d : dfa.getStatesMap().keySet()) {
			if (d.isAcceptState()) continue;
			if ( d.getStateNumber() == Integer.MAX_VALUE ) continue;
			ST st = stlib.getInstanceOf("state");
			st.add("name", "s"+ d.getStateNumber());
			st.add("label", getStateLabel(d));
			dot.add("states", st);
		}

		for (DFAState d : dfa.getStatesMap().keySet()) {
			if ( d.getEdges() !=null ) {
				for (int i = 0; i < d.getEdges().length; i++) {
					DFAState target = d.getEdges()[i];
					if ( target==null) continue;
					if ( target.getStateNumber() == Integer.MAX_VALUE ) continue;
					int ttype = i-1; // we shift up for EOF as -1 for parser
					String label = String.valueOf(ttype);
					if ( isLexer ) label = "'"+getEdgeLabel(new StringBuilder().appendCodePoint(i).toString())+"'";
					else if ( grammar!=null ) label = grammar.getTokenDisplayName(ttype);
					ST st = stlib.getInstanceOf("edge");
					st.add("label", label);
					st.add("src", "s"+ d.getStateNumber());
					st.add("target", "s"+ target.getStateNumber());
					st.add("arrowhead", arrowhead);
					dot.add("edges", st);
				}
			}
		}

		String output = dot.render();
		return Utils.sortLinesInString(output);
	}

	protected String getStateLabel(DFAState s) {
		if ( s==null ) return "null";
		StringBuilder buf = new StringBuilder(250);
		buf.append('s');
		buf.append(s.getStateNumber());
		if (s.isAcceptState()) {
			buf.append("=>").append(s.getPrediction());
		}
		if ( s.getRequiresFullContext()) {
			buf.append("^");
		}
		if ( grammar!=null ) {
			Set<Integer> alts = s.getAltSet();
			if ( alts!=null ) {
				buf.append("\\n");
				// separate alts
				IntegerList altList = new IntegerList();
				altList.addAll(alts);
				altList.sort();
				Set<ATNConfig> configurations = s.getConfigs();
				for (int altIndex = 0; altIndex < altList.size(); altIndex++) {
					int alt = altList.get(altIndex);
					if ( altIndex>0 ) {
						buf.append("\\n");
					}
					buf.append("alt");
					buf.append(alt);
					buf.append(':');
					// get a list of configs for just this alt
					// it will help us print better later
					List<ATNConfig> configsInAlt = new ArrayList<ATNConfig>();
					for (ATNConfig c : configurations) {
						if (c.getAlt() != alt) continue;
						configsInAlt.add(c);
					}
					int n = 0;
					for (int cIndex = 0; cIndex < configsInAlt.size(); cIndex++) {
						ATNConfig c = configsInAlt.get(cIndex);
						n++;
						buf.append(c.toString(null, false));
						if ( (cIndex+1)<configsInAlt.size() ) {
							buf.append(", ");
						}
						if ( n%5==0 && (configsInAlt.size()-cIndex)>3 ) {
							buf.append("\\n");
						}
					}
				}
			}
		}
		String stateLabel = buf.toString();
        return stateLabel;
    }

	public String getDOT(ATNState startState) {
		return getDOT(startState, false);
	}

	public String getDOT(ATNState startState, boolean isLexer) {
		Set<String> ruleNames = grammar.rules.keySet();
		String[] names = new String[ruleNames.size()+1];
		int i = 0;
		for (String s : ruleNames) names[i++] = s;
		return getDOT(startState, names, isLexer);
	}

    /** Return a String containing a DOT description that, when displayed,
     *  will show the incoming state machine visually.  All nodes reachable
     *  from startState will be included.
     */
	public String getDOT(ATNState startState, String[] ruleNames, boolean isLexer) {
		if ( startState==null )	return null;

		// The output DOT graph for visualization
		Set<ATNState> markedStates = new HashSet<ATNState>();
		ST dot = stlib.getInstanceOf("atn");
		dot.add("startState", startState.getStateNumber());
		dot.add("rankdir", rankdir);

		List<ATNState> work = new LinkedList<ATNState>();

		work.add(startState);
		while ( !work.isEmpty() ) {
			ATNState s = work.get(0);
			if ( markedStates.contains(s) ) { work.remove(0); continue; }
			markedStates.add(s);

			// don't go past end of rule node to the follow states
			if ( s instanceof RuleStopState) continue;

			// special case: if decision point, then line up the alt start states
			// unless it's an end of block
//			if ( s instanceof BlockStartState ) {
//				ST rankST = stlib.getInstanceOf("decision-rank");
//				DecisionState alt = (DecisionState)s;
//				for (int i=0; i<alt.getNumberOfTransitions(); i++) {
//					ATNState target = alt.transition(i).target;
//					if ( target!=null ) {
//						rankST.add("states", target.stateNumber);
//					}
//				}
//				dot.add("decisionRanks", rankST);
//			}

			// make a DOT edge for each transition
			ST edgeST;
			for (int i = 0; i < s.getNumberOfTransitions(); i++) {
				Transition edge = s.transition(i);
				if ( edge instanceof RuleTransition) {
					RuleTransition rr = ((RuleTransition)edge);
					// don't jump to other rules, but display edge to follow node
					edgeST = stlib.getInstanceOf("edge");

					String label = "<" + ruleNames[rr.getRuleIndex()];
					if (((RuleStartState) rr.getTarget()).isLeftRecursiveRule()) {
						label += "[" + rr.getPrecedence() + "]";
					}
					label += ">";

					edgeST.add("label", label);
					edgeST.add("src", "s"+ s.getStateNumber());
					edgeST.add("target", "s"+ rr.getFollowState().getStateNumber());
					edgeST.add("arrowhead", arrowhead);
					dot.add("edges", edgeST);
					work.add(rr.getFollowState());
					continue;
				}
				if ( edge instanceof ActionTransition) {
					edgeST = stlib.getInstanceOf("action-edge");
					edgeST.add("label", getEdgeLabel(edge.toString()));
				}
				else if ( edge instanceof AbstractPredicateTransition) {
					edgeST = stlib.getInstanceOf("edge");
					edgeST.add("label", getEdgeLabel(edge.toString()));
				}
				else if ( edge.isEpsilon() ) {
					edgeST = stlib.getInstanceOf("epsilon-edge");
					edgeST.add("label", getEdgeLabel(edge.toString()));
					boolean loopback = false;
					if (edge.getTarget() instanceof PlusBlockStartState) {
						loopback = s.equals(((PlusBlockStartState) edge.getTarget()).getLoopBackState());
					}
					else if (edge.getTarget() instanceof StarLoopEntryState) {
						loopback = s.equals(((StarLoopEntryState) edge.getTarget()).getLoopBackState());
					}
					edgeST.add("loopback", loopback);
				}
				else if ( edge instanceof AtomTransition) {
					edgeST = stlib.getInstanceOf("edge");
					AtomTransition atom = (AtomTransition)edge;
					String label = String.valueOf(atom.getLabel());
					if ( isLexer ) label = "'"+getEdgeLabel(new StringBuilder().appendCodePoint(atom.getLabel()).toString())+"'";
					else if ( grammar!=null ) label = grammar.getTokenDisplayName(atom.getLabel());
					edgeST.add("label", getEdgeLabel(label));
				}
				else if ( edge instanceof SetTransition ) {
					edgeST = stlib.getInstanceOf("edge");
					SetTransition set = (SetTransition)edge;
					String label = set.label().toString();
					if ( isLexer ) label = set.label().toString(true);
					else if ( grammar!=null ) label = set.label().toString(grammar.getVocabulary());
					if ( edge instanceof NotSetTransition ) label = "~"+label;
					edgeST.add("label", getEdgeLabel(label));
				}
				else if ( edge instanceof RangeTransition ) {
					edgeST = stlib.getInstanceOf("edge");
					RangeTransition range = (RangeTransition)edge;
					String label = range.label().toString();
					if ( isLexer ) label = range.toString();
					else if ( grammar!=null ) label = range.label().toString(grammar.getVocabulary());
					edgeST.add("label", getEdgeLabel(label));
				}
				else {
					edgeST = stlib.getInstanceOf("edge");
					edgeST.add("label", getEdgeLabel(edge.toString()));
				}
				edgeST.add("src", "s"+ s.getStateNumber());
				edgeST.add("target", "s"+ edge.getTarget().getStateNumber());
				edgeST.add("arrowhead", arrowhead);
				if (s.getNumberOfTransitions() > 1) {
					edgeST.add("transitionIndex", i);
				}
				else {
					edgeST.add("transitionIndex", false);
				}
				dot.add("edges", edgeST);
				work.add(edge.getTarget());
			}
		}

		// define nodes we visited (they will appear first in DOT output)
		// this is an example of ST's lazy eval :)
		// define stop state first; seems to be a bug in DOT where doublecircle
		// shape only works if we define them first. weird.
//		ATNState stopState = startState.atn.ruleToStopState.get(startState.rule);
//		if ( stopState!=null ) {
//			ST st = stlib.getInstanceOf("stopstate");
//			st.add("name", "s"+stopState.stateNumber);
//			st.add("label", getStateLabel(stopState));
//			dot.add("states", st);
//		}
		for (ATNState s : markedStates) {
			if ( !(s instanceof RuleStopState) ) continue;
			ST st = stlib.getInstanceOf("stopstate");
			st.add("name", "s"+s.getStateNumber());
			st.add("label", getStateLabel(s));
			dot.add("states", st);
		}

		for (ATNState s : markedStates) {
			if ( s instanceof RuleStopState ) continue;
			ST st = stlib.getInstanceOf("state");
			st.add("name", "s"+s.getStateNumber());
			st.add("label", getStateLabel(s));
			st.add("transitions", s.getTransitions());
			dot.add("states", st);
		}

		return dot.render();
	}


    /** Do a depth-first walk of the state machine graph and
     *  fill a DOT description template.  Keep filling the
     *  states and edges attributes.  We know this is an ATN
     *  for a rule so don't traverse edges to other rules and
     *  don't go past rule end state.
     */
//    protected void walkRuleATNCreatingDOT(ST dot,
//                                          ATNState s)
//    {
//        if ( markedStates.contains(s) ) {
//            return; // already visited this node
//        }
//
//        markedStates.add(s.stateNumber); // mark this node as completed.
//
//        // first add this node
//        ST stateST;
//        if ( s instanceof RuleStopState ) {
//            stateST = stlib.getInstanceOf("stopstate");
//        }
//        else {
//            stateST = stlib.getInstanceOf("state");
//        }
//        stateST.add("name", getStateLabel(s));
//        dot.add("states", stateST);
//
//        if ( s instanceof RuleStopState )  {
//            return; // don't go past end of rule node to the follow states
//        }
//
//        // special case: if decision point, then line up the alt start states
//        // unless it's an end of block
//		if ( s instanceof DecisionState ) {
//			GrammarAST n = ((ATNState)s).ast;
//			if ( n!=null && s instanceof BlockEndState ) {
//				ST rankST = stlib.getInstanceOf("decision-rank");
//				ATNState alt = (ATNState)s;
//				while ( alt!=null ) {
//					rankST.add("states", getStateLabel(alt));
//					if ( alt.transition(1) !=null ) {
//						alt = (ATNState)alt.transition(1).target;
//					}
//					else {
//						alt=null;
//					}
//				}
//				dot.add("decisionRanks", rankST);
//			}
//		}
//
//        // make a DOT edge for each transition
//		ST edgeST = null;
//		for (int i = 0; i < s.getNumberOfTransitions(); i++) {
//            Transition edge = (Transition) s.transition(i);
//            if ( edge instanceof RuleTransition ) {
//                RuleTransition rr = ((RuleTransition)edge);
//                // don't jump to other rules, but display edge to follow node
//                edgeST = stlib.getInstanceOf("edge");
//				if ( rr.rule.g != grammar ) {
//					edgeST.add("label", "<"+rr.rule.g.name+"."+rr.rule.name+">");
//				}
//				else {
//					edgeST.add("label", "<"+rr.rule.name+">");
//				}
//				edgeST.add("src", getStateLabel(s));
//				edgeST.add("target", getStateLabel(rr.followState));
//				edgeST.add("arrowhead", arrowhead);
//                dot.add("edges", edgeST);
//				walkRuleATNCreatingDOT(dot, rr.followState);
//                continue;
//            }
//			if ( edge instanceof ActionTransition ) {
//				edgeST = stlib.getInstanceOf("action-edge");
//			}
//			else if ( edge instanceof PredicateTransition ) {
//				edgeST = stlib.getInstanceOf("edge");
//			}
//			else if ( edge.isEpsilon() ) {
//				edgeST = stlib.getInstanceOf("epsilon-edge");
//			}
//			else {
//				edgeST = stlib.getInstanceOf("edge");
//			}
//			edgeST.add("label", getEdgeLabel(edge.toString(grammar)));
//            edgeST.add("src", getStateLabel(s));
//			edgeST.add("target", getStateLabel(edge.target));
//			edgeST.add("arrowhead", arrowhead);
//            dot.add("edges", edgeST);
//            walkRuleATNCreatingDOT(dot, edge.target); // keep walkin'
//        }
//    }

    /** Fix edge strings so they print out in DOT properly;
	 *  generate any gated predicates on edge too.
	 */
    protected String getEdgeLabel(String label) {
		label = label.replace("\\", "\\\\");
		label = label.replace("\"", "\\\"");
		label = label.replace("\n", "\\\\n");
		label = label.replace("\r", "");
        return label;
    }

	protected String getStateLabel(ATNState s) {
		if ( s==null ) return "null";
		String stateLabel = "";

		if (s instanceof BlockStartState) {
			stateLabel += "&rarr;\\n";
		}
		else if (s instanceof BlockEndState) {
			stateLabel += "&larr;\\n";
		}

		stateLabel += String.valueOf(s.getStateNumber());

		if (s instanceof PlusBlockStartState || s instanceof PlusLoopbackState) {
			stateLabel += "+";
		}
		else if (s instanceof StarBlockStartState || s instanceof StarLoopEntryState || s instanceof StarLoopbackState) {
			stateLabel += "*";
		}

		if ( s instanceof DecisionState && ((DecisionState) s).getDecision() >=0 ) {
			stateLabel = stateLabel+"\\nd="+ ((DecisionState) s).getDecision();
		}

		return stateLabel;
	}

}
