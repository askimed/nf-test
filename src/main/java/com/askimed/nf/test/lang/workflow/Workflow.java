package com.askimed.nf.test.lang.workflow;

import java.beans.Transient;
import java.io.File;

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