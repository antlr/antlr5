/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.codegen.model;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.runtime.core.atn.ATN;
import org.antlr.v5.runtime.core.atn.ATNSerializer;
import org.antlr.v5.runtime.core.misc.IntegerList;

/** Represents a serialized ATN that is just a list of signed integers; works for all targets
 *  except for java, which requires a 16-bit char encoding. See {@link SerializedJavaATN}.
 */
public class SerializedATN extends OutputModelObject {
	public int[] serialized;

	public SerializedATN(OutputModelFactory factory) {
		super(factory);
	}

	public SerializedATN(OutputModelFactory factory, ATN atn) {
		super(factory);
		IntegerList data = ATNSerializer.Companion.getSerialized(atn);
		serialized = data.toArray();
	}

	public Object getSerialized() { return serialized; }
}
