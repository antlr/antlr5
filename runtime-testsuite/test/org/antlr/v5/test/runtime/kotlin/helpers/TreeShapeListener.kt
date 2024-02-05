package org.antlr.v5.test.runtime.kotlin.helpers

import org.antlr.v5.runtime.kotlin.ParserRuleContext
import org.antlr.v5.runtime.kotlin.tree.ErrorNode
import org.antlr.v5.runtime.kotlin.tree.ParseTreeListener
import org.antlr.v5.runtime.kotlin.tree.RuleNode
import org.antlr.v5.runtime.kotlin.tree.TerminalNode

class TreeShapeListener : ParseTreeListener {
    override fun visitTerminal(node: TerminalNode) {}
    override fun visitErrorNode(node: ErrorNode) {}
    override fun exitEveryRule(ctx: ParserRuleContext) {}

    override fun enterEveryRule(ctx: ParserRuleContext) {
        for (i in 0 until ctx.childCount) {
            val parent = ctx.getChild(i)!!.readParent()
            check(!(parent !is RuleNode || parent.ruleContext !== ctx)) { "Invalid parse tree shape detected." }
        }
    }

    companion object {
        val INSTANCE: TreeShapeListener = TreeShapeListener()
    }
}
