package com.askimed.nf.test.core.tagquery;

import com.askimed.nf.test.core.ITaggable;

public class NotNode implements TagExpression {

    private final TagExpression operand;

    public NotNode(TagExpression operand) {
        this.operand = operand;
    }

    public TagExpression getOperand() {
        return operand;
    }

    @Override
    public boolean evaluate(ITaggable taggable) {
        return !operand.evaluate(taggable);
    }

}
