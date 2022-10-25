package com.askimed.nf.test.lang;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.opencsv.exceptions.CsvValidationException;

import groovy.json.JsonSlurper;

public class WorkflowMeta {

	public boolean success = true;

	public int exitStatus = 0;

	public String errorMessage = "";

	public String errorReport = "";

	public boolean failed = false;

	public WorkflowTrace trace;

	public List<String> stdout = new Vector<String>();

	public List<String> stderr = new Vector<String>();

	public void loadFromFolder(File folder) {

		File file = new File(folder, "workflow.json");

		if (file.exists()) {
			JsonSlurper jsonSlurper = new JsonSlurper();
			Map<Object, Object> map = (Map<Object, Object>) jsonSlurper.parse(file);
			this.success = getBoolean(map, "success");
			this.failed = !this.success;
			this.exitStatus = getInteger(map, "exitStatus");
			this.errorMessage = getString(map, "errorMessage");
			this.errorReport = getString(map, "errorReport");
		}

		File outFile = new File(folder, "std.out");
		if (outFile.exists()) {
			try {
				stdout = Files.readAllLines(outFile.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File outFileStdErr = new File(folder, "std.err");
		if (outFileStdErr.exists()) {
			try {
				stderr = Files.readAllLines(outFileStdErr.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		File traceFile = new File(folder, "trace.csv");
		if (traceFile.exists()) {
			try {
				trace = new WorkflowTrace(traceFile);
			} catch (CsvValidationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public WorkflowTrace getTrace() {
		if (trace == null) {
			throw new RuntimeException("Error: Tracing is disabled. `workflow.trace` is not supported.");
		}
		return trace;
	}

	private String getString(Map<Object, Object> map, String key) {
		if (map.containsKey(key) && map.get(key) != null) {
			return (String) map.get(key);
		} else {
			return null;
		}
	}

	private Integer getInteger(Map<Object, Object> map, String key) {
		if (map.containsKey(key) && map.get(key) != null) {
			return (Integer) map.get(key);
		} else {
			return 0;
		}
	}

	private Boolean getBoolean(Map<Object, Object> map, String key) {
		if (map.containsKey(key) && map.get(key) != null) {
			return (Boolean) map.get(key);
		} else {
			return true;
		}
	}
	
	public boolean isSuccess() {
		return success;
	}
	
	public boolean isFailed() {
		return failed;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public int getExitStatus() {
		return exitStatus;
	}
	
	public String getErrorReport() {
		return errorReport;
	}
	
	public List<String> getStderr() {
		return stderr;
	}
	
	public List<String> getStdout() {
		return stdout;
	}

}
