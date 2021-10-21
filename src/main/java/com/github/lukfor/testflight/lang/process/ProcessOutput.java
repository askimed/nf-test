package com.github.lukfor.testflight.lang.process;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.github.lukfor.testflight.util.AnsiText;

import groovy.json.JsonSlurper;

public class ProcessOutput extends HashMap<Object, Object> {

	private static final long serialVersionUID = 1L;

	public void loadFromFolder(File folder) {
		for (File file : folder.listFiles()) {
			Map<Object, Object> channel = loadFromFile(file);
			putAll(channel);
		}
	}

	public Map<Object, Object> loadFromFile(File file) {
		JsonSlurper jsonSlurper = new JsonSlurper();
		Map<Object, Object> map = (Map<Object, Object>) jsonSlurper.parse(file);
		return map;
	}

	public void view() {
		System.out
				.println(AnsiText.padding(groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(this)), 4));
	}

}
