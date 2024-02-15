package com.askimed.nf.test.nextflow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.askimed.nf.test.lang.dependencies.IMetaFile;
import com.askimed.nf.test.util.FileUtil;

public class NextflowScript implements IMetaFile {

	private File file;

	private boolean dsl2 = false;

	private List<String> processes = new Vector<String>();

	private List<String> workflows = new Vector<String>();

	private List<String> functions = new Vector<String>();

	private Set<String> dependencies = new HashSet<String>();

	public NextflowScript(File file) {
		this.file = file;
	}

	public void load() throws IOException {
		String script = FileUtil.readFileAsString(file);
		processes = getProcesseNames(script);
		workflows = getWorkflowNames(script);
		functions = getFunctionNames(script);
	}


	public void parseDependencies() throws IOException {
		String script = FileUtil.readFileAsString(file);
		dependencies = getDependencies(file, script);
	}

		@Override
	public String getFilename() {
		return file.getAbsolutePath();
	}

	public boolean isDsl2() {
		return dsl2;
	}

	public List<String> getProcesses() {
		return processes;
	}

	public static List<String> getProcesseNames(String content) {

		List<String> names = new Vector<String>();

		String patternProcessName = "(?i)^\\s*process\\s*(.+)(\\s*\\{|\\{)";

		Pattern r = Pattern.compile(patternProcessName, Pattern.MULTILINE);

		Matcher m = r.matcher(content);
		while (m.find()) {
			names.add(m.group(1).trim());
		}

		return names;
	}

	public List<String> getFunctions() {
		return functions;
	}

	public static List<String> getFunctionNames(String content) {

		List<String> names = new Vector<String>();

		String patternFunctionName = "(?i)^\\s*def\\s*(.+)(\\s*\\(|\\()";

		Pattern r = Pattern.compile(patternFunctionName, Pattern.MULTILINE);

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

		String patternProcessName = "(?i)^\\s*workflow\\s*(.+)(\\s*\\{|\\{)";

		Pattern r = Pattern.compile(patternProcessName, Pattern.MULTILINE);

		Matcher m = r.matcher(content);
		while (m.find()) {
			if (!m.group(1).trim().isEmpty()) {
				names.add(m.group(1).trim());
			}
		}

		return names;
	}

	public Set<String> getDependencies() {
		return dependencies;
	}

	@Override
	public MetaFileType getType() {
		return MetaFileType.SOURCE_FILE;
	}

	public static Set<String> getDependencies(File file, String content) {

		Set<String> dependencies = new HashSet<String>();

		String regex = "(?i)include\\s*\\{\\s*([A-Z_1-9]+(?:\\s+as\\s+[A-Z_]+)?)\\s*\\}\\s*from\\s*['\"](.+?)['\"]";

		Pattern pattern = Pattern.compile(regex,  Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			Path path = null;
			String dependency = matcher.group(2).trim();
			if (dependency.startsWith("/")) {
				continue;
			}
			if (!dependency.endsWith(".nf")){
				dependency += ".nf";
			}
			if (dependency.startsWith("./") || dependency.startsWith("../")) {
				path = Paths.get(file.getParentFile().getAbsolutePath()).resolve(dependency);
			} else {
				path = Paths.get(dependency);
			}
			if (!path.toFile().exists()){
				System.out.println("Warning: Module " + file.getAbsolutePath() + ": Dependency '" + path.toAbsolutePath() + "' not found." );

				continue;
			}
			dependencies.add(path.normalize().toFile().getAbsolutePath());
		}

		return dependencies;

	}

	public static boolean accepts(Path path) {
		return path.getFileName().toString().endsWith(".nf");
	}

}
