package com.askimed.nf.test.lang.function;

import java.io.IOException;

import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;

import groovy.lang.Closure;

public class FunctionTestSuite extends AbstractTestSuite {

	private String function;

	public void function(String function) {
		setFunction(function);
	}

	public void setFunction(String function) {
		this.function = function;
		tag(function);
	}

	public String getFunction() {
		return function;
	}

	public void test(String name, Closure closure) throws IOException {
		addTestClosure(name, closure);
	}

	@Override
	protected ITest getNewTestInstance(String name) {
		FunctionTest test = new FunctionTest(this);
		test.name(name);
		return test;
	}

}