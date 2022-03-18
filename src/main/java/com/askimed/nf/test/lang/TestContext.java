package com.askimed.nf.test.lang;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.askimed.nf.test.lang.process.Process;
import com.askimed.nf.test.lang.workflow.Workflow;
import com.askimed.nf.test.util.FileUtil;

import groovy.lang.Closure;

public class TestContext {

	private ParamsMap params = new ParamsMap();

	private Workflow workflow = new Workflow();

	private Process process = new Process();

	private Closure paramsClosure;

	private Closure processClosure;

	public String baseDir = "lukas";

	public String outputDir = "foreer";

	public TestContext() {

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

	public void params(Closure closure) {
		this.paramsClosure = closure;
	}

	public void evaluateParamsClosure(String baseDir, String outputDir) {
		params.setBaseDir(baseDir);
		params.setOutputDir(outputDir);
		this.baseDir = baseDir;
		this.outputDir = outputDir;

		if (paramsClosure != null) {
			paramsClosure.setDelegate(params);
			paramsClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
			paramsClosure.call();
			paramsClosure.getMetaClass().getProperties();
		}
	}

	public void process(Closure<Object> closure) {
		processClosure = closure;
	}

	public void evaluateProcessClosure() {
		if (processClosure != null) {
			processClosure.setDelegate(this);
			processClosure.setResolveStrategy(Closure.DELEGATE_FIRST);
			Object mapping = processClosure.call();
			if (mapping != null) {
				process.setMapping(mapping.toString());
			}
		}
	}

	public void clean(String path) throws IOException {
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				FileUtil.deleteDirectory(file);
			} else {
				Files.delete(file.toPath());
			}
		}
	}

}
