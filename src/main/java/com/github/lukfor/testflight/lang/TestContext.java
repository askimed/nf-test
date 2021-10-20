package com.github.lukfor.testflight.lang;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.github.lukfor.testflight.util.AnsiText;
import com.github.lukfor.testflight.util.FileUtil;

import groovy.lang.Closure;

public class TestContext {

	private Map<String, Object> params = new HashMap<String, Object>();

	private Map<String, Object> output = new HashMap<String, Object>();

	private Workflow workflow = new Workflow();

	private Process process = new Process();

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
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

	public Map<String, Object> getOutput() {
		return output;
	}

	public void output() {
		System.out.println(
				AnsiText.padding(groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(output)), 4));
	}

	public void params(Closure closure) {
		closure.setDelegate(params);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		closure.call();
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

	public static class Workflow {

		public boolean success = true;

		public int exitCode = 0;

		public boolean failed = false;

		public void setExitCode(int exitCode) {

			this.exitCode = exitCode;
			this.success = (exitCode == 0);
			this.failed = (exitCode != 0);

		}

	}

	public static class Process {

		public boolean success = true;

		public int exitCode = 0;

		public boolean failed = false;

		public void setExitCode(int exitCode) {

			this.exitCode = exitCode;
			this.success = (exitCode == 0);
			this.failed = (exitCode != 0);

		}

	}

}
