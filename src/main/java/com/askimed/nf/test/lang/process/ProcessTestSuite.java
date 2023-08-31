package com.askimed.nf.test.lang.process;

import java.io.IOException;

import com.askimed.nf.test.core.AbstractTestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

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

	public void test(String name,
			@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure)
			throws IOException {

		final ProcessTest test = new ProcessTest(this);	
		test.name(name);
		test.setup(getHomeDirectory());
		closure.setDelegate(test);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();
		addTest(test);

	}

}