package com.askimed.nf.test.lang;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class WorkflowTrace {

	private List<Map<String, Object>> tasks = new Vector<Map<String, Object>>();

	public WorkflowTrace(File file) {

		// TODO: load from csv file

	}

	public void setTasks(List<Map<String, Object>> tasks) {
		this.tasks = tasks;
	}

	public List<Map<String, Object>> getTasks() {
		return tasks;
	}

	public Map<String, String> task(String regex) {
		// TODO: filter all task id that match the regex
		return null;
	}

	// returns number of succeded tasks
	public int success() {
		return -1;
	}

	// return number of failed tasks
	public int failed() {
		return -1;
	}

}
