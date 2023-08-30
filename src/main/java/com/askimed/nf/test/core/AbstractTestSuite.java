package com.askimed.nf.test.core;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.config.Config;

public abstract class AbstractTestSuite implements ITestSuite {

	private String name;

	private String profile = null;

	private File globalConfig = null;

	private File localConfig = null;

	private List<ITest> tests = new Vector<ITest>();

	private String filename;

	private boolean autoSort = true;

	private String options = "";
	
	private String directory = "";
	
	private String homeDirectory = Config.DEFAULT_HOME;

	private List<String> tags = new Vector<String>();

	@Override
	public void configure(Config config) {
		autoSort = config.isAutoSort();
		options = config.getOptions();
		homeDirectory = config.getWorkDir();
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
	
	public String getHomeDirectory() {
		return homeDirectory;
	}

	@Override
	public void setFilename(String filename) {
		this.filename = filename;
		this.directory = new File(filename).getParentFile().getAbsolutePath();
	}

	@Override
	public String getFilename() {
		return filename;
	}

	@Override
	public String getDirectory() {
		return directory;
	}
	
	@Override
	public List<ITest> getTests() {
		return tests;
	}

	protected void addTest(ITest test) {
		tests.add(test);
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

	protected String makeAbsolute(String path) {
		return new File(directory, path).getAbsolutePath();
	}
	
	protected boolean isRelative(String path) {
		return path.startsWith("../") || path.startsWith("./");
	}
	
}
