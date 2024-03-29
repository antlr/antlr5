/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package com.strumenta.antlrkotlin.runtime

// Note(Edoardo): WASI is single threaded at the moment, so a normal list is good enough
public actual typealias CopyOnWriteArrayList<E> = ArrayList<E>
