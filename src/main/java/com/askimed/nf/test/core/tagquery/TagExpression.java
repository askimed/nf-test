package com.askimed.nf.test.core.tagquery;

import com.askimed.nf.test.core.ITaggable;

public interface TagExpression {

    boolean evaluate(ITaggable taggable);

}
