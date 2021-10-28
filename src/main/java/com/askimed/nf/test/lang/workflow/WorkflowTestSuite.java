package com.askimed.nf.test.lang.workflow;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.core.ITestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class WorkflowTestSuite implements ITestSuite {

	private String name;

	private String script = null;

	private String profile = null;

	private File config = null;

	private String workflow;

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

	public void workflow(String workflow) {
		this.workflow = workflow;
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

	@Override
	public void setConfigFile(File config) {
		this.config = config;
	}

	public File getConfig() {
		return config;
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
	}

	public String getWorkflow() {
		return workflow;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void test(String name,
			@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final WorkflowTest test = new WorkflowTest(this);
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