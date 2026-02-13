package com.askimed.nf.test.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Command {

	protected String cmd;

	private String[] params;

	private boolean silent = false;

	private String directory = null;

	private String stdoutFileName = null;

	private String stderrFileName = null;

	private StringBuffer stdout;

	private StringBuffer stderr;

	private Map<String, String> environment = new HashMap<>();

	public Command(String cmd, String... params) {
		this.cmd = cmd;
		this.params = params;
	}

	public Command(String cmd) {
		this.cmd = cmd;
	}

	public void setArgs(String... params) {
		this.params = params;
	}

	public void setParams(List<String> params) {
		this.params = new String[params.size()];
		for (int i = 0; i < params.size(); i++) {
			this.params[i] = params.get(i);
		}
	}

	public void writeStdout(StringBuffer stdout) {
		this.stdout = stdout;
	}

	public void writeStderr(StringBuffer stderr) {
		this.stderr = stderr;
	}


	public void saveStdOut(String filename) {
		this.stdoutFileName = filename;
	}

	public void saveStdErr(String filename) {
		this.stderrFileName = filename;
	}

	public void addEnvironment(String key, String value) {
		environment.put(key, value);
	}

	public int execute() {

		List<String> command = new ArrayList<String>();

		command.add(cmd);

		if (params != null) {
			for (String param : params) {
				command.add(param);
			}
		}

		try {

			ProcessBuilder builder = new ProcessBuilder(command);

			// apply custom environment variables
			if (environment != null) {
				builder.environment().putAll(environment);
			}

			// ensure it works on MacOS
			builder.environment().put("PATH", builder.environment().get("PATH") + ":" + "/usr/local/bin/");
			if (directory != null) {
				builder.directory(new File(directory));
			}

			Process process = builder.start();
			CommandStreamHandler handler = new CommandStreamHandler(process.getInputStream(), stdoutFileName);
			handler.setStringBuffer(stdout);
			handler.setSilent(silent);
			Thread inputStreamHandler = new Thread(handler);

			CommandStreamHandler handler2 = new CommandStreamHandler(process.getErrorStream(), stderrFileName);
			handler2.setStringBuffer(stderr);
			handler2.setSilent(silent);
			Thread errorStreamHandler = new Thread(handler2);

			inputStreamHandler.start();
			errorStreamHandler.start();

			process.waitFor();

			inputStreamHandler.interrupt();
			errorStreamHandler.interrupt();
			inputStreamHandler.join();
			errorStreamHandler.join();

			if (process.exitValue() != 0) {
				return process.exitValue();
			} else {
				process.destroy();
			}

			return 0;

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getDirectory() {
		return directory;
	}

	@Override
	public String toString() {
		String result = cmd;
		if (params != null) {
			for (String param : params) {
				result += " " + param;
			}
		}
		return result;
	}

	public String getName() {
		return cmd;
	}

	public String getExecutedCommand() {
		String executedCommand = cmd;
		if (params != null) {
			for (String param : params) {
				executedCommand += " " + param;
			}
		}
		return executedCommand;
	}

}