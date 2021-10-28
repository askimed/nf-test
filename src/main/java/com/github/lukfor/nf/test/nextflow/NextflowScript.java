package com.github.lukfor.nf.test.nextflow;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.lukfor.nf.test.util.FileUtil;

public class NextflowScript {

	private File file;

	private boolean dsl2 = false;

	private List<String> processes = new Vector<String>();

	private List<String> workflows = new Vector<String>();

	public NextflowScript(File file) {
		this.file = file;
	}

	public void load() throws IOException {
		String script = FileUtil.readFileAsString(file);
		processes = getProcesseNames(script);
		workflows = getWorkflowNames(script);
	}

	public boolean isDsl2() {
		return dsl2;
	}

	public List<String> getProcesses() {
		return processes;
	}

	public static List<String> getProcesseNames(String content) {

		List<String> names = new Vector<String>();

		String patternProcessName = "(?i)process\\s(.*)(\\s\\{|\\{)";

		Pattern r = Pattern.compile(patternProcessName);

		Matcher m = r.matcher(content);
		while (m.find()) {
			names.add(m.group(1).trim());
		}

		return names;
	}

	public List<String> getWorkflows() {
		return workflows;
	}

	public static List<String> getWorkflowNames(String content) {

		List<String> names = new Vector<String>();

		String patternProcessName = "(?i)workflow\\s(.+)(\\s\\{|\\{)";

		Pattern r = Pattern.compile(patternProcessName);

		Matcher m = r.matcher(content);
		while (m.find()) {
			if (!m.group(1).trim().isEmpty()) {
				names.add(m.group(1).trim());
			}
		}

		return names;
	}

}
