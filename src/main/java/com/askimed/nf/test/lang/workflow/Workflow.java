package com.askimed.nf.test.lang.workflow;

public class Workflow {

	public boolean success = true;

	public int exitCode = 0;

	public boolean failed = false;

	private String mapping = "";
	
	private String name = "workflow";
	
	public void setName(String name) {
		this.name = name;
	}

	public void setExitCode(int exitCode) {

		this.exitCode = exitCode;
		this.success = (exitCode == 0);
		this.failed = (exitCode != 0);

	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}

	@Override
	public String toString() {
		return name;
	}

}