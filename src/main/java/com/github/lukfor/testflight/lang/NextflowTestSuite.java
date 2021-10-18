package com.github.lukfor.testflight.lang;

import java.util.List;
import java.util.Vector;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class NextflowTestSuite {

	private String name;

	String script = "main.nf";

	String profile = null;

	List<NextflowTest> tests = new Vector<NextflowTest>();
	
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
			@DelegatesTo(value = NextflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		final NextflowTest dsl = new NextflowTest(this);
		dsl.name(name);	
		closure.setDelegate(dsl);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();
		tests.add(dsl);
		//return dsl;
	}
	
	public List<NextflowTest> getTests() {
		return tests;
	}
	
}