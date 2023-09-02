package com.askimed.nf.test.lang.process;

import java.io.IOException;

import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;

import groovy.lang.Closure;

public class ProcessTestSuite extends AbstractTestSuite {

	private String process;

	public void process(String process) {
		this.process = process;
	}

	public void setProcess(String process) {
		this.process = process;
	}

	public String getProcess() {
		return process;
	}

	public void test(String name, Closure closure) throws IOException {
		addTestClosure(name, closure);
	}

	@Override
	protected ITest getNewTestInstance(String name) {
		ProcessTest test = new ProcessTest(this);
		test.name(name);
		return test;
	}

}