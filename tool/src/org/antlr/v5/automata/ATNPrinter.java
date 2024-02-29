/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.automata;

import org.antlr.v5.runtime.core.state.*;
import org.antlr.v5.runtime.core.transition.*;
import org.antlr.v5.tool.Grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** An ATN walker that knows how to dump them to serialized strings. */
public class ATNPrinter {
	List<ATNState> work;
	Set<ATNState> marked;
	Grammar g;
	ATNState start;

	public ATNPrinter(Grammar g, ATNState start) {
		this.g = g;
		this.start = start;
	}

	public String asString() {
		if ( start==null ) return null;
		marked = new HashSet<ATNState>();

		work = new ArrayList<ATNState>();
		work.add(start);

		StringBuilder buf = new StringBuilder();
		ATNState s;

		while ( !work.isEmpty() ) {
			s = work.remove(0);
			if ( marked.contains(s) ) continue;
			int n = s.getNumberOfTransitions();
//			System.out.println("visit "+s+"; edges="+n);
			marked.add(s);
			for (int i=0; i<n; i++) {
				Transition t = s.transition(i);
				if ( !(s instanceof RuleStopState) ) { // don't add follow states to work
					if ( t instanceof RuleTransition ) work.add(((RuleTransition) t).getFollowState());
					else work.add( t.getTarget() );
				}
				buf.append(getStateString(s));
				if ( t instanceof EpsilonTransition) {
					buf.append("->").append(getStateString(t.getTarget())).append('\n');
				}
				else if ( t instanceof RuleTransition ) {
					buf.append("-").append(g.getRule(((RuleTransition) t).getRuleIndex()).name).append("->").append(getStateString(t.getTarget())).append('\n');
				}
				else if ( t instanceof ActionTransition) {
					ActionTransition a = (ActionTransition)t;
					buf.append("-").append(a.toString()).append("->").append(getStateString(t.getTarget())).append('\n');
				}
				else if ( t instanceof SetTransition) {
					SetTransition st = (SetTransition)t;
					boolean not = st instanceof NotSetTransition;
					if ( g.isLexer() ) {
						buf.append("-").append(not?"~":"").append(st.toString()).append("->").append(getStateString(t.getTarget())).append('\n');
					}
					else {
						buf.append("-").append(not?"~":"").append(st.label().toString(g.getVocabulary())).append("->").append(getStateString(t.getTarget())).append('\n');
					}
				}
				else if ( t instanceof AtomTransition ) {
					AtomTransition a = (AtomTransition)t;
					String label = g.getTokenDisplayName(a.getLabel());
					buf.append("-").append(label).append("->").append(getStateString(t.getTarget())).append('\n');
				}
				else {
					buf.append("-").append(t.toString()).append("->").append(getStateString(t.getTarget())).append('\n');
				}
			}
		}
		return buf.toString();
	}

	String getStateString(ATNState s) {
		int n = s.getStateNumber();
		String stateStr = "s"+n;
		if ( s instanceof StarBlockStartState) stateStr = "StarBlockStart_"+n;
		else if ( s instanceof PlusBlockStartState ) stateStr = "PlusBlockStart_"+n;
		else if ( s instanceof BlockStartState) stateStr = "BlockStart_"+n;
		else if ( s instanceof BlockEndState) stateStr = "BlockEnd_"+n;
		else if ( s instanceof RuleStartState) stateStr = "RuleStart_"+g.getRule(s.getRuleIndex()).name+"_"+n;
		else if ( s instanceof RuleStopState ) stateStr = "RuleStop_"+g.getRule(s.getRuleIndex()).name+"_"+n;
		else if ( s instanceof PlusLoopbackState) stateStr = "PlusLoopBack_"+n;
		else if ( s instanceof StarLoopbackState) stateStr = "StarLoopBack_"+n;
		else if ( s instanceof StarLoopEntryState) stateStr = "StarLoopEntry_"+n;
		return stateStr;
	}
}
