/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.automata.optimization;

import org.antlr.v5.runtime.core.state.ATNState;
import org.antlr.v5.runtime.core.transition.Transition;

import java.util.Objects;

/**
 * Helper class used in ATN optimizers
 */
class InTransition {
	public final Transition transition;
	public final ATNState previousState;
	public final boolean isFollowState;

	public InTransition(Transition transition, ATNState previousState, boolean isFollowState) {
		this.transition = transition;
		this.previousState = previousState;
		this.isFollowState = isFollowState;
	}

	@Override
	public String toString() {
		return previousState + " -> " + transition.getTarget();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		InTransition that = (InTransition) o;
		return isFollowState == that.isFollowState && Objects.equals(transition, that.transition) && Objects.equals(previousState, that.previousState);
	}

	@Override
	public int hashCode() {
		return Objects.hash(transition, previousState, isFollowState);
	}
}
