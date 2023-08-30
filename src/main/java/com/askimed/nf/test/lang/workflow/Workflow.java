package com.askimed.nf.test.lang.workflow;

import java.beans.Transient;
import java.io.File;

import com.askimed.nf.test.lang.WorkflowMeta;
import com.askimed.nf.test.lang.channels.Channels;

public class Workflow extends WorkflowMeta {

	private String mapping = "";

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
		if (out.isEmpty()) {
			throw new RuntimeException("Workflow has no output channels. workflow.out can not be used.");
		}
		return out;
	}

	public void loadOutputChannels(File metaDir, boolean autoSort) {
		out.loadFromFolder(metaDir, autoSort);
	}

	public void viewChannels() {
		out.view();
	}

}