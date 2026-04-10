package com.askimed.nf.test.core;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class TagQuery {

	private List<String> tags = new Vector<String>();

	private List<String> excludeTags = new Vector<String>();

	public TagQuery() {

	}

	public TagQuery(List<String> tags) {
		this.tags = toLowerCase(tags);
	}

	public TagQuery(List<String> tags, List<String> excludeTags) {
		this.tags = toLowerCase(tags);
		this.excludeTags = toLowerCase(excludeTags);
	}

	protected List<String> toLowerCase(List<String> tags) {
		List<String> result = new Vector<String>();
		for (String tag : tags) {
			result.add(tag.toLowerCase());
		}
		return result;
	}

	public boolean matches(ITaggable taggable) {

		if (isExcluded(taggable)) {
			return false;
		}

		if (tags == null || tags.size() == 0) {
			return true;
		}

		if (tags.contains(taggable.getName().toLowerCase())) {
			return true;
		}

		for (String tag : taggable.getTags()) {
			if (tags.contains(tag.toLowerCase())) {
				return true;
			}
		}

		if (taggable.getParent() != null) {
			return matches(taggable.getParent());
		}

		return false;
	}

	private boolean isExcluded(ITaggable taggable) {

		if (excludeTags == null || excludeTags.isEmpty()) {
			return false;
		}

		if (excludeTags.contains(taggable.getName().toLowerCase())) {
			return true;
		}

		for (String tag : taggable.getTags()) {
			if (excludeTags.contains(tag.toLowerCase())) {
				return true;
			}
		}

		if (taggable.getParent() != null) {
			return isExcluded(taggable.getParent());
		}

		return false;
	}

	@Override
	public String toString() {
		return tags.toString();
	}

}
