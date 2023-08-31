package com.askimed.nf.test.lang.function;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.TestContext;

import groovy.lang.Closure;

public class FunctionContext extends TestContext {

	private Function function = new Function();

	private Closure functionClosure;

	public FunctionContext(ITest test) {
		super(test);
	}

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

	public void setName(String name) {
		function.setName(name);
	}

	public void function(Closure<Object> closure) {
		functionClosure = closure;
	}

	public void evaluateFunctionClosure() {
		if (functionClosure == null) {
			return;
		}
		functionClosure.setDelegate(this);
		functionClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
		Object mapping = functionClosure.call();
		if (mapping != null) {
			function.setMapping(mapping.toString());
		}

	}

}
