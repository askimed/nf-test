package com.github.lukfor.testflight.nextflow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.github.lukfor.testflight.util.BinaryFinder;
import com.github.lukfor.testflight.util.Command;

import groovy.json.JsonOutput;

public class NextflowCommand {

	private String binary;

	private File script;

	private String profile;

	private boolean silent = true;

	private Map<String, Object> params;

	public NextflowCommand() {
		binary = new BinaryFinder("nextflow").env("NEXTFLOW_HOME").envPath().path("/usr/local/bin").find();
	}

	public boolean isInstalled() {
		return binary != null;
	}

	public void setScript(File script) {
		this.script = script;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public int execute() throws IOException {

		if (binary == null) {
			throw new IOException("Nextflow Binary not found. Please check PATH or set NEXTFLOW_HOME.");
		}

		File paramsFile = File.createTempFile("params", ".json");
		paramsFile.deleteOnExit();

		writeParamsJson(params, paramsFile);

		List<String> args = new Vector<String>();
		args.add("run");
		args.add(script.getAbsolutePath());
		args.add("-params-file");
		args.add(paramsFile.getAbsolutePath());
		args.add("-ansi-log");
		args.add("false");
		if (profile != null) {
			args.add("-profile");
			args.add(profile);
		}

		Command nextflow = new Command(binary);
		nextflow.setParams(args);
		nextflow.setSilent(silent);
		return nextflow.execute();

	}

	protected void writeParamsJson(Map<String, Object> params, File paramsFile) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(paramsFile));
		writer.write(JsonOutput.toJson(params));
		writer.close();

	}

}
