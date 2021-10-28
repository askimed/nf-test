package com.github.lukfor.nf.test.lang.process;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.github.lukfor.nf.test.core.ITest;
import com.github.lukfor.nf.test.core.ITestSuite;
import com.github.lukfor.nf.test.lang.pipeline.PipelineTest;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class ProcessTestSuite implements ITestSuite {

	private String name;

	private String script = null;

	private String profile = null;

	private File config = null;

	private String process;

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

	public void process(String process) {
		this.process = process;
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

	public void setProcess(String process) {
		this.process = process;
	}

	public String getProcess() {
		return process;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void test(String name,
			@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final ProcessTest test = new ProcessTest(this);
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