package com.github.lukfor.testflight.nextflow;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.lukfor.testflight.util.FileUtil;

public class NextflowScript {

	private File file;

	private boolean dsl2 = false;

	private List<String> processes = new Vector<String>();

	public NextflowScript(File file) {
		this.file = file;
	}

	public void load() throws IOException {
		String script = FileUtil.readFileAsString(file);
		processes = getProcesseNames(script);
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

}
