/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.test.runtime;

import kotlin.Pair;
import org.antlr.v5.test.runtime.java.JavaRuntimeTests;
import org.antlr.v5.test.runtime.states.ExecutedState;
import org.antlr.v5.test.runtime.states.State;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/** This class represents runtime tests for specified runtime.
 *  It pulls data from {@link RuntimeTestDescriptor} and uses junit to trigger tests.
 *  The only functionality needed to execute a test is defined in {@link RuntimeRunner}.
 *  All the various test rig classes derived from this one.
 *  E.g., see {@link JavaRuntimeTests}.
 */
public abstract class RuntimeTests {
	protected abstract RuntimeRunner createRuntimeRunner();

	private final static HashMap<String, RuntimeTestDescriptor[]> testDescriptors = new HashMap<>();
	private final static Map<String, STGroup> cachedTargetTemplates = new HashMap<>();
	private final static StringRenderer rendered = new StringRenderer();

	static {
		File descriptorsDir = new File(Paths.get(RuntimeTestUtils.resourcePath.toString(), "org/antlr/v5/test/runtime/descriptors").toString());
		File[] directoryListing = descriptorsDir.listFiles();
		assert directoryListing != null;
		for (File directory : directoryListing) {
			String groupName = directory.getName();
			if (groupName.startsWith(".")) {
				continue; // Ignore service directories (like .DS_Store in Mac)
			}

			List<RuntimeTestDescriptor> descriptors = new ArrayList<>();

			File[] descriptorFiles = directory.listFiles();
			assert descriptorFiles != null;
			for (File descriptorFile : descriptorFiles) {
				String name = descriptorFile.getName().replace(".txt", "");
				if (name.startsWith(".")) {
					continue;
				}

				String text;
				try {
					text = new String(Files.readAllBytes(descriptorFile.toPath()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				descriptors.add(RuntimeTestDescriptorParser.parse(name, text, descriptorFile.toURI()));
			}

			testDescriptors.put(groupName, descriptors.toArray(new RuntimeTestDescriptor[0]));
		}

		for (String key : CustomDescriptors.descriptors.keySet()) {
			RuntimeTestDescriptor[] descriptors = CustomDescriptors.descriptors.get(key);
			RuntimeTestDescriptor[] existedDescriptors = testDescriptors.putIfAbsent(key, descriptors);
			if (existedDescriptors != null) {
				testDescriptors.put(key, Stream.concat(Arrays.stream(existedDescriptors), Arrays.stream(descriptors))
						.toArray(RuntimeTestDescriptor[]::new));
			}
		}
	}

	@TestFactory
	@Execution(ExecutionMode.CONCURRENT)
	public List<DynamicNode> runtimeTests() {
		List<DynamicNode> result = new ArrayList<>();

		for (String group : testDescriptors.keySet()) {
			ArrayList<DynamicNode> descriptorTests = new ArrayList<>();
			RuntimeTestDescriptor[] descriptors = testDescriptors.get(group);
			for (RuntimeTestDescriptor descriptor : descriptors) {
				descriptorTests.add(dynamicTest(descriptor.name, descriptor.uri, () -> {
					try (RuntimeRunner runner = createRuntimeRunner()) {
						test(descriptor, runner);
					}
				}));
			}

			Path descriptorGroupPath = Paths.get(RuntimeTestUtils.resourcePath.toString(), "descriptors", group);
			result.add(dynamicContainer(group, descriptorGroupPath.toUri(), Arrays.stream(descriptorTests.toArray(new DynamicNode[0]))));
		}

		return result;
	}

	private static void test(RuntimeTestDescriptor descriptor, RuntimeRunner runner) {
		String targetName = runner.getLanguage();
		if (descriptor.ignore(targetName)) {
			System.out.println("Ignore " + descriptor);
			return;
		}

		Pair<String[], String[]> allGrammars = prepareGrammars(descriptor, runner);

		RunOptions runOptions = new RunOptions(
				allGrammars.getFirst(),
				allGrammars.getSecond(),
				true,
				true,
				descriptor.startRule,
				descriptor.input,
				false,
				descriptor.showDiagnosticErrors,
				descriptor.traceATN,
				descriptor.showDFA,
				Stage.Execute,
				null,
				descriptor.predictionMode,
				descriptor.buildParseTree,
				null
		);

		State result = runner.run(runOptions);

		checkOutput(descriptor, result, runner);
	}

	private static Pair<String[], String[]> prepareGrammars(RuntimeTestDescriptor descriptor, RuntimeRunner runner) {
		String targetName = runner.getLanguage();

		STGroup targetTemplates;
		synchronized (cachedTargetTemplates) {
			targetTemplates = cachedTargetTemplates.get(targetName);
			if (targetTemplates == null) {
				ClassLoader classLoader = RuntimeTests.class.getClassLoader();
				URL templates = classLoader.getResource("org/antlr/v5/test/runtime/templates/" + targetName + ".test.stg");
				assert templates != null;
				targetTemplates = new STGroupFile(templates, "UTF-8", '<', '>');
				targetTemplates.registerRenderer(String.class, rendered);
				cachedTargetTemplates.put(targetName, targetTemplates);
			}
		}

		List<String> grammars = new ArrayList<>(descriptor.grammars.length);
		List<String> slaveGrammars = new ArrayList<>(descriptor.slaveGrammars.length);
		for (String grammar : descriptor.grammars) {
			grammars.add(prepareGrammar(targetTemplates, grammar));
		}
		for (String grammar : descriptor.slaveGrammars) {
			slaveGrammars.add(prepareGrammar(targetTemplates, grammar));
		}

		return new Pair<>(grammars.toArray(new String[0]), slaveGrammars.toArray(new String[0]));
	}

	private static String prepareGrammar(STGroup targetTemplates, String grammar) {
		STGroup g = new STGroup('<', '>');
		g.registerRenderer(String.class, rendered);
		g.importTemplates(targetTemplates);
		ST grammarST = new ST(g, grammar);
		return grammarST.render();
	}

	public static void checkOutput(RuntimeTestDescriptor descriptor, State state, RuntimeRunner runner) {
		String tempDirPath = runner.getTempDirPath();
		String testDescriptor = "\nTest: " + descriptor.name + "\nTest directory: " + tempDirPath;

		ExecutedState executedState = null;
		boolean shouldFail = false;
		if (state instanceof ExecutedState) {
			executedState = (ExecutedState)state;
			if (executedState.exception != null) {
				shouldFail = true;
			}
		}
		else {
			shouldFail = true;
		}

		if (shouldFail || !descriptor.errors.equals(executedState.errors) || !descriptor.output.equals(executedState.output)) {
			runner.setSaveTestDir(true);
		}

		if (shouldFail) {
			fail(testDescriptor + "\n" + state.getErrorMessage());
		}

		assertEquals(descriptor.errors, executedState.errors, testDescriptor + "\nUnexpected or missing parse errors");
		assertEquals(descriptor.output, executedState.output, testDescriptor);
	}
}
