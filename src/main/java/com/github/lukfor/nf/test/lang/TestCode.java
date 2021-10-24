package com.github.lukfor.nf.test.lang;

import groovy.lang.Closure;

public class TestCode {

	private Closure closure;

	public TestCode(Closure closure) {
		this.closure = closure;
	}

	public void execute(TestContext context) {
		closure.setDelegate(context);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		closure.call();

	}

	public Closure getClosure() {
		return closure;
	}

}
