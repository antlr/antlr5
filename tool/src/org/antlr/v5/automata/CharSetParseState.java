/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.automata;

import org.antlr.v5.runtime.core.misc.IntervalSet;

import java.util.Objects;

public class CharSetParseState {
	public enum Mode {
		NONE,
		PREV_CODE_POINT,
		PREV_PROPERTY
	}

	public static final CharSetParseState NONE = new CharSetParseState(Mode.NONE, false, -1, IntervalSet.Companion.getEMPTY_SET());

	public final Mode mode;
	public final boolean inRange;
	public final int prevCodePoint;
	public final IntervalSet prevProperty;

	public CharSetParseState(Mode mode, boolean inRange, int prevCodePoint, IntervalSet prevProperty) {
		this.mode = mode;
		this.inRange = inRange;
		this.prevCodePoint = prevCodePoint;
		this.prevProperty = prevProperty;
	}

	@Override
	public String toString() {
		return String.format(
				"%s mode=%s inRange=%s prevCodePoint=%d prevProperty=%s",
				super.toString(),
				mode,
				inRange,
				prevCodePoint,
				prevProperty);
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof CharSetParseState)) {
			return false;
		}
		CharSetParseState that = (CharSetParseState) other;
		if (this == that) {
			return true;
		}
		return Objects.equals(this.mode, that.mode) &&
				Objects.equals(this.inRange, that.inRange) &&
				Objects.equals(this.prevCodePoint, that.prevCodePoint) &&
				Objects.equals(this.prevProperty, that.prevProperty);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mode, inRange, prevCodePoint, prevProperty);
	}
}
