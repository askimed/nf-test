package com.askimed.nf.test.lang.workflow;

import java.beans.Transient;

import com.askimed.nf.test.lang.WorkflowMeta;
import com.askimed.nf.test.lang.channels.Channels;

public class Workflow extends WorkflowMeta {
	
	private String mapping = "";

	private String name = "workflow";

	private Channels out = new Channels();
	
	public void setName(String name) {
		this.name = name;
	}

	@Transient
	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}
	
	public Channels getOut() {
		return out;
	}

	@Override
	public String toString() {
		return name;
	}

}