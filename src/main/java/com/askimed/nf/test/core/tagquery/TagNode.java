package com.askimed.nf.test.core.tagquery;

import com.askimed.nf.test.core.ITaggable;

public class TagNode implements TagExpression {

    private final String tag;

    public TagNode(String tag) {
        this.tag = tag.toLowerCase();
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean evaluate(ITaggable taggable) {
        return matches(taggable);
    }

    private boolean matches(ITaggable taggable) {
        if (tag.equals(taggable.getName().toLowerCase())) {
            return true;
        }
        for (String t : taggable.getTags()) {
            if (tag.equals(t.toLowerCase())) {
                return true;
            }
        }
        if (taggable.getParent() != null) {
            return matches(taggable.getParent());
        }
        return false;
    }

}
