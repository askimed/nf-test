package com.github.lukfor.testflight.lang;

import java.util.List;
import java.util.Vector;

import com.github.lukfor.testflight.core.ITest;
import com.github.lukfor.testflight.core.ITestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class WorkflowTestSuite implements ITestSuite {

	private String name;

	String script = "main.nf";

	String profile = null;

	List<ITest> tests = new Vector<ITest>();
	
	public void name(String name) {
		this.name = name;
	}

	public void script(String script) {
		this.script = script;	}

	public void profile(String profile) {
		this.profile = profile;
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
	
	public String getScript() {
		return script;
	}
	
	public void setScript(String script) {
		this.script = script;
	}

	public void debug(String message) {
		System.out.println(message);
	}

	public void test(String name, 
			@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		final WorkflowTest dsl = new WorkflowTest(this);
		dsl.name(name);	
		closure.setDelegate(dsl);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();
		tests.add(dsl);
		//return dsl;
	}
	
	@Override
	public List<ITest> getTests() {
		return tests;
	}
	
}