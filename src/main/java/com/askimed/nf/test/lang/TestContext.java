package com.askimed.nf.test.lang;

import java.io.File;
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

	private String baseDir = "";
	
	private String outputDir = "";
	
	public TestContext(String baseDir, String outputDir) {
		this.baseDir = baseDir;
		this.outputDir = outputDir;
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
		params = new ParamsMap();
		params.setBaseDir(baseDir);
		params.setOutputDir(outputDir);
		closure.setDelegate(params);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		closure.call();		
		closure.getMetaClass().getProperties();
		System.out.println("----> " + closure.getProperty("var1"));

	}

	public void process(Closure<Object> closure) {
		closure.setDelegate(this);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		closure.call();
		Object mapping = closure.call();
		if (mapping != null) {
			process.setMapping(mapping.toString());
		}
	}

	public void clean(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				FileUtil.deleteDirectory(file);
			} else {
				file.delete();
			}
		}
	}

}
