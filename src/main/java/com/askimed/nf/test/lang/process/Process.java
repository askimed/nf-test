package com.askimed.nf.test.lang.process;

import com.askimed.nf.test.lang.WorkflowMeta;

public class Process extends WorkflowMeta{

	private ChannelsOutput out = new ChannelsOutput();

	private String mapping = "";

	private String name = "process";

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