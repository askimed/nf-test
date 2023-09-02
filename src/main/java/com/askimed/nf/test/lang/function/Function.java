package com.askimed.nf.test.lang.function;

import java.io.File;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.lang.WorkflowMeta;

import groovy.json.JsonSlurper;

public class Function extends WorkflowMeta {

	private Object result = null;

	private String mapping = "";

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public void loadResult(File folder) {
		File file = new File(folder, AbstractTest.FILE_FUNCTION_JSON);

		if (file.exists()) {
			JsonSlurper jsonSlurper = new JsonSlurper();
			result = jsonSlurper.parse(file);
		}

	}

}