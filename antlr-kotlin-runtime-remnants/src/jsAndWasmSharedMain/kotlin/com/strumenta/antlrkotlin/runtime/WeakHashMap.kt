/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.kotlinruntime

// Note(Edoardo): this is implemented as an HashMap in the JS target,
//  so let's keep it as it is
public actual typealias WeakHashMap<K, V> = HashMap<K, V>
