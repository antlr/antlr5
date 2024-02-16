package org.antlr.v5.runtime.core.atn

/**
 * Represents the type of recognizer an ATN applies to.
 *
 * @author Sam Harwell
 */
public enum class ATNType {
  /**
   * A lexer grammar.
   */
  LEXER,

  /**
   * A parser grammar.
   */
  PARSER,
}