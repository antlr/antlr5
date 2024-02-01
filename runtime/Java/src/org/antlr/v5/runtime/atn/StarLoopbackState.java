/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.atn;

public final class StarLoopbackState extends ATNState {
	public final StarLoopEntryState getLoopEntryState() {
		return (StarLoopEntryState)transition(0).target;
	}

	@Override
	public int getStateType() {
		return STAR_LOOP_BACK;
	}
}
