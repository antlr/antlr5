package org.antlr.v5.test.runtime.kotlin;

import org.antlr.v5.test.runtime.RuntimeRunner;
import org.antlr.v5.test.runtime.RuntimeTests;

public class KotlinRuntimeTests extends RuntimeTests {
	@Override
	protected RuntimeRunner createRuntimeRunner() {
		return new KotlinRunner();
	}
}
