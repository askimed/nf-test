package com.askimed.nf.test.core;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class TagQuery {

	private List<String> tags = new Vector<String>();

	public TagQuery() {

	}

	public TagQuery(String... tags) {
		this.tags = toLowerCase(Arrays.asList(tags));
	}

	public TagQuery(List<String> tags) {
		this.tags = toLowerCase(tags);
	}

	protected List<String> toLowerCase(List<String> tags) {
		List<String> result = new Vector<String>();
		for (String tag : tags) {
			result.add(tag.toLowerCase());
		}
		return result;
	}

	public boolean matches(ITaggable taggable) {

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

	@Override
	public String toString() {
		return tags.toString();
	}

}
