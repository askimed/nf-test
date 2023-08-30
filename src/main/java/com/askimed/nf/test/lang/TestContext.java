package com.askimed.nf.test.lang;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.extensions.Snapshot;
import com.askimed.nf.test.lang.function.Function;
import com.askimed.nf.test.lang.process.Process;
import com.askimed.nf.test.lang.workflow.Workflow;

import groovy.lang.Closure;

public class TestContext {

	private ParamsMap params = new ParamsMap();

	private Workflow workflow = new Workflow();

	private Process process = new Process();

	private Function function = new Function();

	private Closure paramsClosure;

	private Closure processClosure;

	public String baseDir = "";

	public String projectDir = "";

	public String launchDir = "";

	public String workDir = "";

	public String outputDir = "";

	public ITest test;

	public TestContext(ITest test) {
		this.test = test;
	}

	public void setName(String name) {
		process.setName(name);
	}

	public ParamsMap getParams() {
		return params;
	}

	public void setParams(ParamsMap params) {
		this.params = params;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public Process getProcess() {
		return process;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
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

	public void process(Closure<Object> closure) {
		processClosure = closure;
	}

	public void evaluateProcessClosure() {
		if (processClosure == null) {
			return;
		}
		processClosure.setDelegate(this);
		processClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
		Object mapping = processClosure.call();
		if (mapping != null) {
			process.setMapping(mapping.toString());
		}

	}

	public void workflow(Closure<Object> closure) {
		processClosure = closure;
	}

	public void evaluateWorkflowClosure() {
		if (processClosure == null) {
			return;
		}
		processClosure.setDelegate(this);
		processClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
		Object mapping = processClosure.call();
		if (mapping != null) {
			workflow.setMapping(mapping.toString());
		}

	}

	public void function(Closure<Object> closure) {
		processClosure = closure;
	}

	public void evaluateFunctionClosure() {
		if (processClosure == null) {
			return;
		}
		processClosure.setDelegate(this);
		processClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
		Object mapping = processClosure.call();
		if (mapping != null) {
			process.setMapping(mapping.toString());
		}

	}

	public Snapshot snapshot(Object... object) {
		return new Snapshot(object, test);
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

	public void loadParams(String filename) throws CompilationFailedException, ClassNotFoundException, IOException {
		params.load(filename);
	}

}
