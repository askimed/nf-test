package com.askimed.nf.test.lang.process;

import java.io.File;

import com.askimed.nf.test.lang.WorkflowMeta;

public class Process extends WorkflowMeta {

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
		if (out.isEmpty()) {
			throw new RuntimeException("Process has no output channels. process.out can not be used.");
		}
		return out;

	}
	
	public void loadOutputChannels(File metaDir, boolean autoSort) {
		out.loadFromFolder(metaDir, autoSort);
	}

	public void viewChannels() {
		out.view();
	}

	@Override
	public String toString() {
		return name;
	}

}