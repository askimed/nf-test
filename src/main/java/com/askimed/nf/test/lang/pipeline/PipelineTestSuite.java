package com.askimed.nf.test.lang.pipeline;

import java.io.IOException;

import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;

import groovy.lang.Closure;

public class PipelineTestSuite extends AbstractTestSuite {

	public PipelineTestSuite() {
		name("main.nf");
	}

	public void test(String name, Closure closure) throws IOException {
		addTestClosure(name, closure);
	}

	@Override
	protected ITest getNewTestInstance(String name) {
		PipelineTest test = new PipelineTest(this);
		test.name(name);
		return test;
	}

}