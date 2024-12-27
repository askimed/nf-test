package com.askimed.nf.test.core;

import groovy.util.Eval;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagQueryExpression extends TagQuery {

	private String query;

	public TagQueryExpression(String query) {
		this.query = query;
	}

	public boolean matches(ITaggable taggable) {
		if (query == null || query.trim().isEmpty()) {
			return true;
		}

		Map<String, Object> bindingContext = createBindingContext(taggable);

		try {
			return (Boolean) Eval.me("tags", bindingContext.get("tags"), query);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid query: " + query, e);
		}
	}

	private Map<String, Object> createBindingContext(ITaggable taggable) {
		Map<String, Boolean> tagMap = new HashMap<>();
		Map<String, Object> context = new HashMap<>();

		// Add tags from the current taggable
		taggable.getTags().forEach(tag -> {
			tagMap.put(tag.toLowerCase(), true);
		});
		tagMap.put(taggable.getName(), true);

		// Add parent tags recursively
		ITaggable parent = taggable.getParent();
		while (parent != null) {
			parent.getTags().forEach(tag -> {
				tagMap.put(tag.toLowerCase(), true);
			});
			tagMap.put(parent.getName(), true);
			parent = parent.getParent();
		}

		context.put("tags", new DefaultTagMap(tagMap)); // Map for key-based access
		context.put("query", query); // The actual query as a string

		return context;
	}

	@Override
	public String toString() {
		return query;
	}


	// Custom map to handle missing keys
	private static class DefaultTagMap extends HashMap<String, Boolean> {
		public DefaultTagMap(Map<String, Boolean> map) {
			super(map);
		}

		@Override
		public Boolean get(Object key) {
			return super.getOrDefault(key, false);
		}
	}
}
