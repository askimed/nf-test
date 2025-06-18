package com.askimed.nf.test.nextflow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.askimed.nf.test.util.BinaryFinder;
import com.askimed.nf.test.util.Command;

import groovy.json.JsonOutput;

public class NextflowCommand {

	private String binary;

	private String script;

	private List<String> profiles = new Vector<String>();

	private List<File> configs = new Vector<File>();

	private File out;

	private File err;

	private File workDir;

	private File launchDir;

	private boolean debug = false;

	private File trace = null;

	private File log = null;

	private File paramsFile;

	private Map<String, Object> params;

	private String options = "";

	private static boolean verbose = false;

	public static String ERROR = "Nextflow Binary not found. Please check if Nextflow is in a directory accessible by your $PATH variable or set $NEXTFLOW_HOME.";

	public NextflowCommand() {
		binary = new BinaryFinder("nextflow").env("NEXTFLOW_HOME").envPath().path("/usr/local/bin").find();
	}

	public boolean isInstalled() {
		return binary != null;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public void addProfile(String profile) {
		if (profile == null) {
			return;
		}
		if (!profile.startsWith("+")) {
			this.profiles.clear();
		}
		String[] tiles = profile.split(",");
		for (String tile : tiles) {
			this.profiles.add(tile.replace("+", "").trim());
		}
	}

	public void addConfig(File config) {
		if (config == null) {
			return;
		}
		this.configs.add(config);
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public static void setVerbose(boolean verbose) {
		NextflowCommand.verbose = verbose;
	}

	public void setTrace(File trace) {
		this.trace = trace;
	}

	public File getTrace() {
		return trace;
	}

	public void setOut(File out) {
		this.out = out;
	}

	public File getOut() {
		return out;
	}

	public void setErr(File err) {
		this.err = err;
	}

	public File getErr() {
		return err;
	}

	public void setLog(File log) {
		this.log = log;
	}

	public File getLog() {
		return log;
	}

	public void setWorkDir(File workDir) {
		this.workDir = workDir;
	}

	public File getWorkDir() {
		return launchDir;
	}

	public void setLaunchDir(File launchDir) {
		this.launchDir = launchDir;
	}

	public File getLaunchDir() {
		return launchDir;
	}

	public void setParamsFile(File paramsFile) {
		this.paramsFile = paramsFile;
	}

	public File getParamsFile() {
		return paramsFile;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	public String getOptions() {
		return options;
	}

	public int execute() throws IOException {

		if (binary == null) {
			System.out.println("HEY BINARU WHERE ARE YOU!");
			throw new IOException(ERROR);
		}

		if (paramsFile == null) {
			paramsFile = File.createTempFile("params", ".json");
			paramsFile.deleteOnExit();
		}

		writeParamsJson(params, paramsFile);

		List<String> args = new Vector<String>();
		if (!verbose) {
			args.add("-quiet");
		}
		if (log != null) {
			args.add("-log");
			args.add(log.getAbsolutePath());
		}
		args.add("run");
		args.add(script);
		for (File config : configs) {
			args.add("-c");
			args.add(config.getAbsolutePath());
		}
		args.add("-params-file");
		args.add(paramsFile.getAbsolutePath());
		args.add("-ansi-log");
		args.add("false");
		if (!profiles.isEmpty()) {
			args.add("-profile");
			args.add(String.join(",", profiles));
		}
		if (trace != null) {
			args.add("-with-trace");
			args.add(trace.getAbsolutePath());
		}
		if (workDir != null) {
			args.add("-w");
			args.add(workDir.getAbsolutePath());
		}

		args.addAll(parseOptions(options));

		Command nextflow = new Command(binary);
		if (launchDir != null) {
			nextflow.setDirectory(launchDir.getAbsolutePath());
		}
		nextflow.setParams(args);
		if (verbose) {
			System.out.println("");
		}
		nextflow.setSilent(!verbose);
		if (out != null) {
			nextflow.saveStdOut(out.getAbsolutePath());
		}
		if (err != null) {
			nextflow.saveStdErr(err.getAbsolutePath());
		}
		if (debug) {
			//System.out.println();
			System.out.println("    Profiles: " + profiles);
			System.out.println("    Configs: " + configs);
			System.out.println("    Command: " + nextflow.getExecutedCommand());
		}

		int result = nextflow.execute();
		if (debug) {
			printDebugInfo();
		}
		if (verbose || debug) {
			System.out.print("    ");
		}
		return result;

	}

	protected void printDebugInfo() throws IOException {
		printStdout();
		printStderr();
		printLog();
	}

	protected void printStdout() throws IOException {
		if (out != null) {
			System.out.println();
			System.out.println("    Stdout:");
			List<String> lines = Files.readAllLines(out.toPath(), Charset.defaultCharset());
			for (String line : lines) {
				System.out.println("      " + line);
			}
			System.out.println();
		}
	}

	protected void printStderr() throws IOException {
		if (err != null) {
			System.out.println("    Stderr:");
			List<String> lines = Files.readAllLines(err.toPath(), Charset.defaultCharset());
			;
			for (String line : lines) {
				System.out.println("      " + line);
			}
			System.out.println();
		}
	}

	protected void printLog() throws IOException {
		if (log != null) {
			System.out.println("    Nextflow Output:");
			List<String> lines = NextflowLog.parseLines(log, NextflowLogLevel.INFO);
			for (String line : lines) {
				System.out.println("      " + line);
			}
			System.out.println();
		}
	}

	public int printVersion() throws IOException {

		if (binary == null) {
			throw new IOException(ERROR);
		}

		List<String> args = new Vector<String>();
		args.add("-version");

		Command nextflow = new Command(binary);
		nextflow.setParams(args);
		nextflow.setSilent(false);

		return nextflow.execute();

	}

	private static String version = null;

	public static String getVersion(){
		if (version == null){
			try {
				version = new NextflowCommand().parseVersion();
			} catch (Exception e){
				version = "unknown";
			}
		}
		return version;
	}

	public String parseVersion() throws IOException {

		if (binary == null) {
			throw new IOException(ERROR);
		}

		List<String> args = new Vector<String>();
		args.add("-version");

		Command nextflow = new Command(binary);
		nextflow.setParams(args);
		nextflow.setSilent(true);
		StringBuffer output = new StringBuffer();
		nextflow.writeStdout(output);
		nextflow.execute();
		String versionPattern = "version (\\d+\\.\\d+\\.\\d+)";
		Pattern pattern = Pattern.compile(versionPattern);
		Matcher matcher = pattern.matcher(output);

		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return "unknown";
		}

	}



	protected void writeParamsJson(Map<String, Object> params, File paramsFile) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(paramsFile));
		writer.write(JsonOutput.toJson(params));
		writer.close();

	}

	public static List<String> parseOptions(String options) {
		List<String> list = new Vector<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(options);
		while (m.find()) {
			list.add(m.group(1).replace("\"", ""));
		}
		return list;
	}

}
