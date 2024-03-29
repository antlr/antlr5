/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.test.runtime.javascript;

import org.antlr.v5.test.runtime.*;
import org.antlr.v5.test.runtime.states.CompiledState;
import org.antlr.v5.test.runtime.states.GeneratedState;
import org.stringtemplate.v4.ST;

import java.io.IOException;
import java.nio.file.Paths;

import static org.antlr.v5.test.runtime.FileUtils.writeFile;

public class NodeRunner extends RuntimeRunner {
	@Override
	public String getLanguage() {
		return "JavaScript";
	}

	@Override
	public String getRuntimeToolName() { return "node"; }

	private final static String normalizedRuntimePath = getRuntimePath("JavaScript").replace('\\', '/');
	private final static String newImportAntlrString =
			"import antlr4 from 'file://" + normalizedRuntimePath + "/src/antlr4/index.node.js'";

	@Override
	protected CompiledState compile(RunOptions runOptions, GeneratedState generatedState) {
		for (GeneratedFile generatedFile : generatedState.generatedFiles) {
			try {
				FileUtils.replaceInFile(Paths.get(getTempDirPath(), generatedFile.name),
						"import antlr4 from 'antlr4';",
						newImportAntlrString);
			} catch (IOException e) {
				return new CompiledState(generatedState, e);
			}
		}

		writeFile(getTempDirPath(), "package.json",
			RuntimeTestUtils.getTextFromResource("org/antlr/v5/test/runtime/helpers/package_js.json"));

		return new CompiledState(generatedState, null);
	}

	@Override
	protected void addExtraRecognizerParameters(ST template) {
		template.add("runtimePath", normalizedRuntimePath);
	}
}
