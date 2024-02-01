/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package com.strumenta.antlrkotlin.runtime

/**
 * Returns whether we are running on Node.js, or not.
 */
internal fun isNodeJs(): Boolean =
  js(
    """
    (typeof process !== 'undefined'
      && process.versions != null
      && process.versions.node != null) ||
    (typeof window !== 'undefined'
      && typeof window.process !== 'undefined'
      && window.process.versions != null
      && window.process.versions.node != null)
    """
  ).unsafeCast<Boolean>()
