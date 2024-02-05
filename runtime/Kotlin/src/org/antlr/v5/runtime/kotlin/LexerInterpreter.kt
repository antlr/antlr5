/*
 * Copyright (c) 2012-present The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v5.runtime.kotlin

import org.antlr.v5.runtime.kotlin.atn.ATN
import org.antlr.v5.runtime.kotlin.atn.ATNType
import org.antlr.v5.runtime.kotlin.atn.LexerATNSimulator
import org.antlr.v5.runtime.kotlin.atn.PredictionContextCache
import org.antlr.v5.runtime.kotlin.dfa.DFA

public open class LexerInterpreter : Lexer {
  private val _decisionToDFA: Array<DFA>
  private val _sharedContextCache = PredictionContextCache()
  private val _grammarFileName: String
  private val _vocabulary: Vocabulary
  private val _atn: ATN
  private val _tokenNames: Array<String>
  private val _ruleNames: Array<String>
  private val _channelNames: Array<String>
  private val _modeNames: Array<String>
  private var _interpreter: LexerATNSimulator

  override var interpreter: LexerATNSimulator
    get() = _interpreter
    set(value) {
      _interpreter = value
    }

  override val grammarFileName: String
    get() = _grammarFileName

  override val vocabulary: Vocabulary
    get() = _vocabulary

  override val atn: ATN
    get() = _atn

  @Deprecated("Use vocabulary instead", ReplaceWith("vocabulary"))
  override val tokenNames: Array<String>
    get() = _tokenNames

  override val ruleNames: Array<String>
    get() = _ruleNames

  override val channelNames: Array<String>
    get() = _channelNames

  override val modeNames: Array<String>
    get() = _modeNames

  @Suppress("ConvertSecondaryConstructorToPrimary")
  public constructor(
    grammarFileName: String,
    vocabulary: Vocabulary,
    ruleNames: Collection<String>,
    channelNames: Collection<String>,
    modeNames: Collection<String>,
    atn: ATN,
    input: CharStream,
  ) : super(input) {
    if (atn.grammarType != ATNType.LEXER) {
      throw IllegalArgumentException("The ATN must be a lexer ATN.")
    }

    this._grammarFileName = grammarFileName
    this._atn = atn

    _tokenNames = Array(atn.maxTokenType) {
      vocabulary.getDisplayName(it)
    }

    this._ruleNames = ruleNames.toTypedArray()
    this._channelNames = channelNames.toTypedArray()
    this._modeNames = modeNames.toTypedArray()
    this._vocabulary = vocabulary

    _decisionToDFA = Array(atn.numberOfDecisions) {
      DFA(atn.getDecisionState(it)!!, it)
    }

    @Suppress("LeakingThis")
    _interpreter = LexerATNSimulator(this, atn, _decisionToDFA, _sharedContextCache)
  }
}
