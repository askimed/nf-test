package com.askimed.nf.test.lang;

import java.util.HashMap;

public class ParamsMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public String baseDir = "lukas";
	
	public String outputDir = "";

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
		put("baseDir", baseDir);
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
		put("outputDir", outputDir);
	}

	
	public String getBaseDir() {
		return baseDir;
	}
	
	public String getOutputDir() {
		return outputDir;
	}

}
