package com.askimed.nf.test.lang.workflow;

import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.Dependency;
import com.askimed.nf.test.lang.TestContext;

import groovy.lang.Closure;

public class WorkflowContext extends TestContext {

	private Closure workflowClosure;

	private Workflow workflow = new Workflow();

	private List<Dependency> dependencies = new Vector<Dependency>();

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

	public void run(String process, Closure closure) {
		Dependency dependency = new Dependency(process, closure);
		dependencies.add(dependency);
	}

	public List<Dependency> getDependencies() {
		return dependencies;
	}

}
