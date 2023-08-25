package com.askimed.nf.test.lang.workflow;

import java.beans.Transient;

import com.askimed.nf.test.lang.WorkflowMeta;
import com.askimed.nf.test.lang.process.ChannelsOutput;

public class Workflow extends WorkflowMeta {

	private String mapping = "";

	private ChannelsOutput out = new ChannelsOutput();

	@Transient
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}

	public ChannelsOutput getOut() {
		return out;
	}

}