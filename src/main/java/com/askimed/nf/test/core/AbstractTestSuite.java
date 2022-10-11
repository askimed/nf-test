package com.askimed.nf.test.core;

import java.io.File;
import java.util.List;
import java.util.Vector;

public abstract class AbstractTestSuite implements ITestSuite {

	private String name;

	private String profile = null;

	private File globalConfig = null;

	private File localConfig = null;

	private List<ITest> tests = new Vector<ITest>();

	private String filename;

	public void name(String name) {
		this.name = name;
	}

	public void profile(String profile) {
		this.profile = profile;
	}

	public void config(String config) {
		this.localConfig = new File(config);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getProfile() {
		return profile;
	}

	@Override
	public void setGlobalConfigFile(File globalConfig) {
		this.globalConfig = globalConfig;
	}

	public File getGlobalConfigFile() {
		return globalConfig;
	}

	public void setLocalConfig(File localConfig) {
		this.localConfig = localConfig;
	}

	public File getLocalConfig() {
		return localConfig;
	}

	@Override
	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public List<ITest> getTests() {
		return tests;
	}

	protected void addTest(ITest test) {
		tests.add(test);
		test.setTestSuite(this);
	}

}
