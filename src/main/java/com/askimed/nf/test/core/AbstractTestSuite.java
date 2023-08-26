package com.askimed.nf.test.core;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.lang.extensions.SnapshotFile;

public abstract class AbstractTestSuite implements ITestSuite {

	private String name;

	private String profile = null;

	private File globalConfig = null;

	private File localConfig = null;

	private List<ITest> tests = new Vector<ITest>();

	private String filename;

	private boolean autoSort = true;

	private String options = "";

	private SnapshotFile snapshotFile;

	private List<String> tags = new Vector<String>();

	@Override
	public void configure(Config config) {
		autoSort = config.isAutoSort();
		options = config.getOptions();
	}

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

	public void autoSort(boolean autoSort) {
		this.autoSort = autoSort;
	}

	public boolean isAutoSort() {
		return autoSort;
	}

	public void options(String options) {
		this.options = options;
	}

	public String getOptions() {
		return options;
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

	public void tag(String tag) {
		tags.add(tag);
	}

	@Override
	public List<String> getTags() {
		return tags;
	}

	@Override
	public ITaggable getParent() {
		return null;
	}

	@Override
	public boolean hasSkippedTests() {
		for (ITest test : getTests()) {
			if (test.isSkipped()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public SnapshotFile getSnapshot() {
		if (snapshotFile == null) {
			snapshotFile = SnapshotFile.loadByTestSuite(this);
		}
		return snapshotFile;
	}
	
	@Override
	public boolean hasSnapshotLoaded() {
		return (snapshotFile != null);
	}
}
