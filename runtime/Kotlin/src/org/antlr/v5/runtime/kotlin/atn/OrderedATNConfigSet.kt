/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin.atn

import org.antlr.v5.runtime.kotlin.misc.ObjectEqualityComparator

/**
 * @author Sam Harwell
 */
public class OrderedATNConfigSet : ATNConfigSet() {
  public class LexerConfigHashSet : AbstractConfigHashSet(ObjectEqualityComparator.INSTANCE)

  init {
    configLookup = LexerConfigHashSet()
  }
}
