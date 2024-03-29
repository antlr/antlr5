/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.core.transition

import org.antlr.v5.runtime.core.state.ATNState

/**
 * @author Sam Harwell
 */
public abstract class AbstractPredicateTransition(target: ATNState) : Transition(target)
