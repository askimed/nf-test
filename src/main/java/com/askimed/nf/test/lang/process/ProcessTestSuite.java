package com.askimed.nf.test.lang.process;

import com.askimed.nf.test.core.AbstractTestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class ProcessTestSuite extends AbstractTestSuite {

	private String script = null;

	private String process;

	public void script(String script) {
		this.script = script;
	}

	public void process(String process) {
		this.process = process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getProcess() {
		return process;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void test(String name,
			@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final ProcessTest test = new ProcessTest(this);
		test.name(name);
		closure.setDelegate(test);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();
		addTest(test);

	}

}