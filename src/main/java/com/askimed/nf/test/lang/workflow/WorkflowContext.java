package com.askimed.nf.test.lang.workflow;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.TestContext;

import groovy.lang.Closure;

public class WorkflowContext extends TestContext {

	private Closure workflowClosure;

	private Workflow workflow = new Workflow();

	public WorkflowContext(ITest test) {
		super(test);
	}
	
	public void workflow(Closure<Object> closure) {
		workflowClosure = closure;
	}

	public void evaluateWorkflowClosure() {
		if (workflowClosure == null) {
			return;
		}
		workflowClosure.setDelegate(this);
		workflowClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
		Object mapping = workflowClosure.call();
		if (mapping != null) {
			workflow.setMapping(mapping.toString());
		}

	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

}
