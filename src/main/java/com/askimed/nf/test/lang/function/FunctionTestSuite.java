package com.askimed.nf.test.lang.function;

import java.io.IOException;

import com.askimed.nf.test.core.AbstractTestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class FunctionTestSuite extends AbstractTestSuite {

	private String function;

	public void function(String function) {
		this.function = function;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public void test(String name,
			@DelegatesTo(value = FunctionTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure)
			throws IOException {

		final FunctionTest test = new FunctionTest(this);
		test.name(name);	
		test.setup(getHomeDirectory());
		closure.setDelegate(test);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		addTest(test);

	}

}