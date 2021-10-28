package com.askimed.nf.test.lang;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Trace {

	private List<Map<String, Object>> tasks = new Vector<Map<String, Object>>();

	public Trace(File file) {

		// TODO: load from csv file

	}

	public void setTasks(List<Map<String, Object>> tasks) {
		this.tasks = tasks;
	}

	public List<Map<String, Object>> getTasks() {
		return tasks;
	}

}
