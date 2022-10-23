package com.askimed.nf.test.plugins;

import java.io.File;

public class InstalledPlugin {

	private File path;

	private PluginRelease release;

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public PluginRelease getRelease() {
		return release;
	}

	public void setRelease(PluginRelease release) {
		this.release = release;
	}

}
