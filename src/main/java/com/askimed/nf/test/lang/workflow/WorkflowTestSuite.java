package com.askimed.nf.test.lang.workflow;

import com.askimed.nf.test.core.AbstractTestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class WorkflowTestSuite extends AbstractTestSuite {

	private String script = null;

	private String workflow;

	public void script(String script) {
		this.script = script;
	}

	public void workflow(String workflow) {
		this.workflow = workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public String getWorkflow() {
		return workflow;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void test(String name,
			@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final WorkflowTest test = new WorkflowTest(this);
		test.name(name);
		closure.setDelegate(test);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();
		addTest(test);

	}

}