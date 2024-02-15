/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.core.info

import org.antlr.v5.runtime.core.TokenStream
import org.antlr.v5.runtime.core.atn.ATNConfigSet
import org.antlr.v5.runtime.core.jvm.BitSet

/**
 * This class represents profiling event information for an ambiguity.
 *
 * Ambiguities are decisions where a particular input resulted in an SLL
 * conflict, followed by LL prediction also reaching a conflict state
 * (indicating a true ambiguity in the grammar).
 *
 * This event may be reported during SLL prediction in cases where the
 * conflicting SLL configuration set provides sufficient information to
 * determine that the SLL conflict is truly an ambiguity. For example, if none
 * of the ATN configurations in the conflicting SLL configuration set have
 * traversed a global follow transition (i.e.
 * [ATNConfig.reachesIntoOuterContext] is `0` for all configurations), then
 * the result of SLL prediction for that input is known to be equivalent to the
 * result of LL prediction for that input.
 *
 * In some cases, the minimum represented alternative in the conflicting LL
 * configuration set is not equal to the minimum represented alternative in the
 * conflicting SLL configuration set. Grammars and inputs which result in this
 * scenario are unable to use [PredictionMode.SLL], which in turn means
 * they cannot use the two-stage parsing strategy to improve parsing performance
 * for that input.
 *
 * @param decision The decision number
 * @param configs The final configuration set identifying the ambiguous
 *   alternatives for the current input
 * @param ambigAlts The set of alternatives in the decision that lead to a valid parse.
 *   The predicted alt is the min(ambigAlts)
 * @param input The input token stream
 * @param startIndex The start index for the current prediction
 * @param stopIndex The index at which the ambiguity was identified during prediction
 * @param fullCtx `true` if the ambiguity was identified during LL prediction,
 *   otherwise `false` if the ambiguity was identified during SLL prediction
 *
 * @see ParserATNSimulator.reportAmbiguity
 * @see ANTLRErrorListener.reportAmbiguity
 *
 * @since 4.3
 */
@Suppress("MemberVisibilityCanBePrivate")
public class AmbiguityInfo(
    decision: Int,
    configs: ATNConfigSet?,
    public var ambigAlts: BitSet,
    input: TokenStream,
    startIndex: Int,
    stopIndex: Int,
    fullCtx: Boolean,
) : DecisionEventInfo(decision, configs, input, startIndex, stopIndex, fullCtx)
