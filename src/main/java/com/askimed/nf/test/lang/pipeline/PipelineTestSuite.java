package com.askimed.nf.test.lang.pipeline;

import com.askimed.nf.test.core.AbstractTestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class PipelineTestSuite extends AbstractTestSuite {

	private String script = "main.nf";

	public void script(String script) {
		this.script = script;
	}

	public String getScript() {
		if (script != null && isRelative(script)) {
			return makeAbsolute(script);
		} else {
			return script;
		}
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void test(String name,
			@DelegatesTo(value = PipelineTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final PipelineTest test = new PipelineTest(this);
		test.name(name);
		closure.setDelegate(test);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		addTest(test);
	}

}