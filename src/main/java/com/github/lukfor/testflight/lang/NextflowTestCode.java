package com.github.lukfor.testflight.lang;

import groovy.lang.Closure;

public class NextflowTestCode {

	private Closure closure;

	public NextflowTestCode(Closure closure) {
		this.closure = closure;
	}

	public void execute(NextflowTestContext context) {
		closure.setDelegate(context);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		closure.call();

	}

	public Closure getClosure() {
		return closure;
	}

}
