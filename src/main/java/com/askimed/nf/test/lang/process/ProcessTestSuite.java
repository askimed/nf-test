package com.askimed.nf.test.lang.process;

import java.io.IOException;

import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.TestCode;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class ProcessTestSuite extends AbstractTestSuite {

	private String process;

	private TestCode setup;
	
	public void process(String process) {
		setProcess(process);
	}

	public void setProcess(String process) {
		this.process = process;
		tag(process);
	}

	public String getProcess() {
		return process;
	}

	public void test(String name, Closure closure) throws IOException {
		addTestClosure(name, closure);
	}

	public void setup(@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new TestCode(closure);
	}
	
	public TestCode getSetup() {
		return setup;
	}
	
	@Override
	protected ITest getNewTestInstance(String name) {
		ProcessTest test = new ProcessTest(this);
		test.name(name);
		return test;
	}

}