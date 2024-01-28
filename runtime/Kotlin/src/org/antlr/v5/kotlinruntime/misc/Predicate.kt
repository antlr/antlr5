// Copyright 2017-present Strumenta and contributors, licensed under Apache 2.0.
// Copyright 2024-present Strumenta and contributors, licensed under BSD 3-Clause.

package org.antlr.v5.kotlinruntime.misc

public interface Predicate<T> {
  public fun test(t: T): Boolean
}
