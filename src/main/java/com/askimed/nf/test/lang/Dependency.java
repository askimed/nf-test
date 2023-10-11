package com.askimed.nf.test.lang;

import groovy.lang.Closure;

public class Dependency {

	private String script;

	private String name;

	private String mapping;

	public Dependency(String name, Closure closure) {
		this.name = name;
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

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

}
