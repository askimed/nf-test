package com.askimed.nf.test.lang;

import java.io.File;
import java.util.Map;

import com.askimed.nf.test.util.FileUtil;

import groovy.json.JsonSlurper;

public class WorkflowMeta {

	public boolean success = true;

	public int exitStatus = 0;

	public String errorMessage = "";

	public String errorReport = "";

	public boolean failed = false;

	public WorkflowTrace trace;

	public void loadFromFolder(File folder) {

		File file = new File(FileUtil.path(folder.getAbsolutePath(), "workflow.json"));

		if (file.exists()) {
			JsonSlurper jsonSlurper = new JsonSlurper();
			Map<Object, Object> map = (Map<Object, Object>) jsonSlurper.parse(file);
			this.success = getBoolean(map, "success");
			this.failed = !this.success;
			this.exitStatus = getInteger(map, "exitStatus");
			this.errorMessage = getString(map, "errorMessage");
			this.errorReport = getString(map, "errorReport");
		}
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

}
