/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.misc;

import org.antlr.v5.runtime.core.misc.IntervalSet;

import java.util.Objects;

public abstract class CharParseResult {
	public final int startIndex;
	public final int length;

	private CharParseResult(int startIndex, int stopIndex) {
		this.startIndex = Math.max(startIndex, 0);
		this.length = Math.max(stopIndex - startIndex, 0);
	}

	public int getEndIndex() { return startIndex + length; }

	@Override
	public boolean equals(Object other) {
		if (other == null || !getClass().equals(other.getClass())) {
			return false;
		}
		CharParseResult that = (CharParseResult) other;
		if (this == that) {
			return true;
		}
		return Objects.equals(this.startIndex, that.startIndex) && Objects.equals(this.length, that.length);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getClass(), startIndex, length);
	}

	public static class Invalid extends CharParseResult {
		public Invalid(int startIndex, int stopIndex) {
			super(startIndex, stopIndex);
		}
	}

	public static class CodePoint extends CharParseResult {
		public final int codePoint;

		public CodePoint(int codePoint, int startIndex, int stopIndex) {
			super(startIndex, stopIndex);
			this.codePoint = codePoint;
		}

		@Override
		public boolean equals(Object other) {
			return super.equals(other) && Objects.equals(this.codePoint, ((CodePoint)other).codePoint);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), codePoint);
		}
	}

	public static class Property extends CharParseResult {
		public final IntervalSet propertyIntervalSet;

		public Property(IntervalSet propertyIntervalSet, int startIndex, int stopIndex) {
			super(startIndex, stopIndex);
			this.propertyIntervalSet = propertyIntervalSet;
		}

		@Override
		public boolean equals(Object other) {
			return super.equals(other) && Objects.equals(this.propertyIntervalSet, ((Property)other).propertyIntervalSet);
		}

		@Override
		public int hashCode() {
			return Objects.hash(super.hashCode(), propertyIntervalSet);
		}
	}
}
