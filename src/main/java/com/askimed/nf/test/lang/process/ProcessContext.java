package com.askimed.nf.test.lang.process;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.TestContext;

import groovy.lang.Closure;

public class ProcessContext extends TestContext {

	private Process process = new Process();

	private Closure processClosure;

	public ProcessContext(ITest test) {
		super(test);
	}

	public void setName(String name) {
		process.setName(name);
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public void process(Closure<Object> closure) {
		processClosure = closure;
	}

	public void evaluateProcessClosure() {
		if (processClosure == null) {
			return;
		}
		processClosure.setDelegate(this);
		processClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
		Object mapping = processClosure.call();
		if (mapping != null) {
			process.setMapping(mapping.toString());
		}

	}
	
}
