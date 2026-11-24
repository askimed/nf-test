package com.askimed.nf.test.core.tagquery;

import com.askimed.nf.test.core.ITaggable;

public class AndNode implements TagExpression {

    private final TagExpression left;
    private final TagExpression right;

    public AndNode(TagExpression left, TagExpression right) {
        this.left = left;
        this.right = right;
    }

    public TagExpression getLeft() {
        return left;
    }

    public TagExpression getRight() {
        return right;
    }

    @Override
    public boolean evaluate(ITaggable taggable) {
        return left.evaluate(taggable) && right.evaluate(taggable);
    }

}
