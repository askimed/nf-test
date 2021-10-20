package com.github.lukfor.testflight.lang;

import groovy.lang.Closure;

public class ExternalTestCode {

	private Closure<String> closure;

	public ExternalTestCode(Closure<String> closure) {
		this.closure = closure;
	}

	public String getCode(TestContext context) {
		closure.setDelegate(context);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		return closure.call();

	}

	public Closure<String> getClosure() {
		return closure;
	}

}
