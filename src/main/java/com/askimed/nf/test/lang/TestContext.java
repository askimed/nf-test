package com.askimed.nf.test.lang;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.extensions.Snapshot;
import com.askimed.nf.test.lang.workflow.Workflow;

import groovy.lang.Closure;

public class TestContext {

	private ParamsMap params = new ParamsMap();

	private Closure paramsClosure;

	public String baseDir = "";

	public String projectDir = "";

	public String launchDir = "";

	public String workDir = "";

	public String outputDir = "";

	public ITest test;

	private String name;

	private WorkflowMeta workflow = new WorkflowMeta();

	public TestContext(ITest test) {
		this.test = test;
	}

	public void init(String baseDir, String outputDir) {
		params.setBaseDir(baseDir);
		params.setOutputDir(outputDir);
		this.baseDir = baseDir;
		this.outputDir = outputDir;
	}

	public ParamsMap getParams() {
		return params;
	}

	public void setParams(ParamsMap params) {
		this.params = params;
	}

	public void params(Closure closure) {
		this.paramsClosure = closure;
	}

	public void evaluateParamsClosure() {
		if (paramsClosure == null) {
			return;
		}
		paramsClosure.setDelegate(params);
		paramsClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
		paramsClosure.call();
		paramsClosure.getMetaClass().getProperties();
		params.evaluateNestedClosures();

	}

	public WorkflowMeta getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public Snapshot snapshot(Object... object) {
		return new Snapshot(object, test);
	}

	public void loadParams(String filename) throws CompilationFailedException, ClassNotFoundException, IOException {
		params.load(filename);
	}

	public void init(AbstractTest test) {
		params.setBaseDir(test.baseDir);
		params.setProjectDir(test.baseDir);
		params.setLaunchDir(test.launchDir.getAbsolutePath());
		params.setWorkDir(test.workDir.getAbsolutePath());
		params.setOutputDir(test.outputDir.getAbsolutePath());

		this.baseDir = test.baseDir;
		this.projectDir = test.baseDir;
		this.launchDir = test.launchDir.getAbsolutePath();
		this.workDir = test.workDir.getAbsolutePath();
		this.outputDir = test.outputDir.getAbsolutePath();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
