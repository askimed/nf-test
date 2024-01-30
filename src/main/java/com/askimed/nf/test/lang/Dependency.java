package com.askimed.nf.test.lang;

import groovy.lang.Closure;

import java.util.Map;

public class Dependency {

	private String script;

	private String name;

	private String alias;

	private String mapping;

	public static final String ATTRIBUTE_ALIAS = "alias";

	public Dependency(String name, Map<String, Object> attributes, Closure closure) {
		this.name = name;
		if (attributes.containsKey(ATTRIBUTE_ALIAS)) {
			this.alias = attributes.get(ATTRIBUTE_ALIAS).toString();
		}
		closure.setDelegate(this);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		closure.call();
	}

	public void script(String script) {
		this.script = script;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void process(Closure closure) {
		closure.setDelegate(this);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		Object mapping = closure.call();
		if (mapping != null) {
			this.mapping = mapping.toString();
		}
	}

	public void workflow(Closure closure) {
		process(closure);
	}

	public void function(Closure closure) {
		process(closure);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public boolean hasAlias() {
		return alias != null;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

}
