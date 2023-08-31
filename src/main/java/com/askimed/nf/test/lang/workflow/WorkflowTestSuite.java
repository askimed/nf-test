package com.askimed.nf.test.lang.workflow;

import java.io.IOException;

import com.askimed.nf.test.core.AbstractTestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class WorkflowTestSuite extends AbstractTestSuite {

	private String workflow;

	public void workflow(String workflow) {
		this.workflow = workflow;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public String getWorkflow() {
		return workflow;
	}

	public void test(String name,
			@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure)
			throws IOException {

		final WorkflowTest test = new WorkflowTest(this);	
		test.name(name);
		test.setup(getHomeDirectory());
		closure.setDelegate(test);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();
		addTest(test);

	}

}