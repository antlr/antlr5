/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v5.runtime.kotlin

public object System {
  public interface PrintStream {
    /**
     * Prints the line separator to the standard output stream.
     */
    public fun println()

    /**
     * Prints the given message and the line separator to the standard output stream.
     */
    public fun println(message: String)

    /**
     * Prints the given message to the standard output stream.
     */
    public fun print(message: String)
  }

  public var out: PrintStream = StdPrintStream
  public var err: PrintStream = ErrPrintStream

  public fun getenv(name: String, defaultValue: String? = null): String? =
    platformGetEnv(name) ?: defaultValue

  public fun <T> arraycopy(src: Array<T>, srcPos: Int, dest: Array<T>, destPos: Int, length: Int) {
    src.copyInto(dest, destPos, srcPos, srcPos + length)
  }

  public fun arraycopy(src: IntArray, srcPos: Int, dest: IntArray, destPos: Int, length: Int) {
    src.copyInto(dest, destPos, srcPos, srcPos + length)
  }

  private object StdPrintStream : PrintStream {
    override fun println(): Unit =
      kotlin.io.println()

    override fun println(message: String): Unit =
      kotlin.io.println(message)

    override fun print(message: String): Unit =
      kotlin.io.print(message)
  }

  private object ErrPrintStream : PrintStream {
    override fun println(): Unit =
      platformPrintErrLn()

    override fun println(message: String): Unit =
      platformPrintErrLn(message)

    override fun print(message: String): Unit =
      platformPrintErr(message)
  }
}
