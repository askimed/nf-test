package com.askimed.nf.test.lang.function;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.core.ITestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class FunctionTestSuite implements ITestSuite {

	private String name;

	private String script = null;

	private String profile = null;

	private File config = null;

	private String function;

	private List<ITest> tests = new Vector<ITest>();

	public void name(String name) {
		this.name = name;
	}

	public void script(String script) {
		this.script = script;
	}

	public void profile(String profile) {
		this.profile = profile;
	}

	public void function(String function) {
		this.function = function;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getProfile() {
		return profile;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	@Override
	public void setConfigFile(File config) {
		this.config = config;
	}

	public File getConfig() {
		return config;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void test(String name,
			@DelegatesTo(value = FunctionTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final FunctionTest test = new FunctionTest(this);
		test.name(name);
		closure.setDelegate(test);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();
		tests.add(test);

	}

	public List<ITest> getTests() {
		return tests;
	}

}