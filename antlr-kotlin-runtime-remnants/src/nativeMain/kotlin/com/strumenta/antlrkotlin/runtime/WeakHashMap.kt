/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package com.strumenta.antlrkotlin.runtime

// TODO(Edoardo): implement real weak keys.
//  See kotlinlang.org/api/latest/jvm/stdlib/kotlin.native.ref
//  for classes and functions useful for a possible implementation
public actual typealias WeakHashMap<K, V> = HashMap<K, V>
