package com.askimed.nf.test.lang.workflow;

import com.askimed.nf.test.lang.WorkflowMeta;
import com.askimed.nf.test.lang.process.ChannelsOutput;

public class Workflow extends WorkflowMeta {
	private String mapping = "";

	private String name = "workflow";

	private ChannelsOutput out = new ChannelsOutput();
	
	public void setName(String name) {
		this.name = name;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}
	
	public ChannelsOutput getOut() {
		return out;
	}

	@Override
	public String toString() {
		return name;
	}

}