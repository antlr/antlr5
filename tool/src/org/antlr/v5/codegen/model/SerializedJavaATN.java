package org.antlr.v5.codegen.model;

import org.antlr.v5.codegen.OutputModelFactory;
import org.antlr.v5.codegen.Target;
import org.antlr.v5.runtime.core.atn.ATN;
import org.antlr.v5.runtime.core.atn.ATNSerializer;
import org.antlr.v5.runtime.core.misc.IntegerList;

import static org.antlr.v5.runtime.core.misc.IntsEncoderKt.encodeIntsWith16BitWords;

/** A serialized ATN for the java target, which requires we use strings and 16-bit unicode values */
public class SerializedJavaATN extends SerializedATN {
	private final String[] serializedAsString;
	private final String[][] segments;

	public SerializedJavaATN(OutputModelFactory factory, ATN atn) {
		super(factory);
		IntegerList data = ATNSerializer.Companion.getSerialized(atn);
		data = encodeIntsWith16BitWords(data);

		int size = data.size();
		Target target = factory.getGenerator().getTarget();
		int segmentLimit = target.getSerializedATNSegmentLimit();
		segments = new String[(int)(((long)size + segmentLimit - 1) / segmentLimit)][];
		int segmentIndex = 0;

		for (int i = 0; i < size; i += segmentLimit) {
			int segmentSize = Math.min(i + segmentLimit, size) - i;
			String[] segment = new String[segmentSize];
			segments[segmentIndex++] = segment;
			for (int j = 0; j < segmentSize; j++) {
				segment[j] = target.encodeInt16AsCharEscape(data.get(i + j));
			}
		}

		serializedAsString = segments[0]; // serializedAsString is valid if only one segment
	}

	public Object getSerialized() { return serializedAsString; }
	public String[][] getSegments() { return segments; }
}
