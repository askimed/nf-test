package com.askimed.nf.test.lang.pipeline;

import java.io.IOException;

import com.askimed.nf.test.core.AbstractTestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class PipelineTestSuite extends AbstractTestSuite {

	public PipelineTestSuite() {
		name("main.nf");
	}

	public void test(String name,
			@DelegatesTo(value = PipelineTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure)
			throws IOException {

		final PipelineTest test = new PipelineTest(this);
		test.name(name);
		test.setup(getHomeDirectory());
		closure.setDelegate(test);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		addTest(test);
	}

}