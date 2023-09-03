package com.askimed.nf.test.lang.workflow;

import java.io.IOException;

import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;

import groovy.lang.Closure;

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

	public void test(String name, Closure closure) throws IOException {
		addTestClosure(name, closure);
	}

	@Override
	protected ITest getNewTestInstance(String name) {
		WorkflowTest test = new WorkflowTest(this);
		test.name(name);
		return test;
	}


}