package org.antlr.v5.runtime.core.misc


/** Given a list of integers representing a serialized ATN, encode values too large to fit into 15 bits
 * as two 16bit values. We use the high bit (0x8000_0000) to indicate values requiring two 16 bit words.
 * If the high bit is set, we grab the next value and combine them to get a 31-bit value. The possible
 * input int values are [-1,0x7FFF_FFFF].
 *
 * | compression/encoding                         | uint16 count | type            |
 * | -------------------------------------------- | ------------ | --------------- |
 * | 0xxxxxxx xxxxxxxx                            | 1            | uint (15 bit)   |
 * | 1xxxxxxx xxxxxxxx yyyyyyyy yyyyyyyy          | 2            | uint (16+ bits) |
 * | 11111111 11111111 11111111 11111111          | 2            | int value -1    |
 *
 * This is only used (other than for testing) by [org.antlr.v4.codegen.model.SerializedJavaATN]
 * to encode ints as char values for the java target, but it is convenient to combine it with the
 * #decodeIntsEncodedAs16BitWords that follows as they are a pair (I did not want to introduce a new class
 * into the runtime). Used only for Java Target.
 */
public fun encodeIntsWith16BitWords(data: IntegerList): IntegerList {
    val data16 = IntegerList((data.size() * 1.5).toInt())
    for (i in 0 until data.size()) {
        var v = data[i]
        if (v == -1) { // use two max uint16 for -1
            data16.add(0xFFFF)
            data16.add(0xFFFF)
        } else if (v <= 0x7FFF) {
            data16.add(v)
        } else { // v > 0x7FFF
            if (v >= 0x7FFFFFFF) { // too big to fit in 15 bits + 16 bits? (+1 would be 8000_0000 which is bad encoding)
                throw UnsupportedOperationException("Serialized ATN data element[$i] = $v doesn't fit in 31 bits")
            }
            v = v and 0x7FFFFFFF // strip high bit (sentinel) if set
            data16.add((v shr 16) or 0x8000) // store high 15-bit word first and set high bit to say word follows
            data16.add((v and 0xFFFF)) // then store lower 16-bit word
        }
    }
    return data16
}

/**
 * Convert a list of chars (16 uint) that represent a serialized and compressed list of ints for an ATN.
 */
public fun decodeIntsEncodedAs16BitWords(data16: CharArray, trimToSize: Boolean = false): IntArray {
    // Will be strictly smaller, but we waste bit of space to avoid copying during initialization of parsers
    val data = IntArray(data16.size)
    var i = 0
    var i2 = 0

    while (i < data16.size) {
        val v = data16[i++]

        if (v.code and 0x8000 == 0) {
            // Hi-bit not set? Implies 1-word value
            // 7 bit int
            data[i2++] = v.code
        } else {
            // Hi.bit set. Implies 2-word value
            val vnext = data16[i++]

            if (v.code == 0xFFFF && vnext.code == 0xFFFF) { // Is it -1?
                data[i2++] = -1
            } else {
                // 31-bit int
                data[i2++] = (v.code and 0x7FFF) shl 16 or (vnext.code and 0xFFFF)
            }
        }
    }

    if (trimToSize) {
        return data.copyOf(i2)
    }

    return data
}
