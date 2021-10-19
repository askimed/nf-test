package com.github.lukfor.testflight.lang;

import groovy.lang.Closure;

public class WorkflowTestCode {

	private Closure closure;

	public WorkflowTestCode(Closure closure) {
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
