/* Copyright (c) 2012-present The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import ParseTree from "./ParseTree.js";

export default class RuleNode extends ParseTree {

    get ruleContext() {
        throw new Error("missing interface implementation")
    }
}
