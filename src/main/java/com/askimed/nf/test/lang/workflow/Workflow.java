package com.askimed.nf.test.lang.workflow;

import com.askimed.nf.test.lang.WorkflowMeta;

public class Workflow extends WorkflowMeta {
	private String mapping = "";

	private String name = "workflow";

	public void setName(String name) {
		this.name = name;
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