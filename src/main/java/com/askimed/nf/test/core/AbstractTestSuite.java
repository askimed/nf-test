package com.askimed.nf.test.core;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.lang.extensions.SnapshotFile;

import groovy.lang.Closure;

public abstract class AbstractTestSuite implements ITestSuite {

	private String script = null;

	private String name;

	private List<String> profiles = new Vector<String>();;

	private File globalConfig = null;

	private File localConfig = null;

	private List<ITest> tests = new Vector<ITest>();

	private String filename;

	private boolean autoSort = true;

	private String options = "";

	private String directory = "";

	private File homeDirectory = new File(Config.DEFAULT_HOME);

	private SnapshotFile snapshotFile;

	private boolean failedTests = false;

	private List<String> tags = new Vector<String>();

	private List<NamedClosure> testClosures = new Vector<NamedClosure>();

	private Config config;

	@Override
	public void configure(Config config) {
		this.config = config;
		autoSort = config.isAutoSort();
		options = config.getOptions();
		homeDirectory = new File(config.getWorkDir());
		if (config.getProfile() != null) {
			addProfile(config.getProfile());
		}
	}

	public void script(String script) {
		this.script = script;
	}

	public String getScript() {
		if (script != null && isRelative(script)) {
			return makeAbsolute(script);
		} else {
			return script;
		}
	}

	protected void addTestClosure(String name, Closure closure) {
		// TODO: check if name is unique!

		testClosures.add(new NamedClosure(name, closure));
	}

	public void evalualteTestClosures() throws Throwable {

		for (NamedClosure namedClosure : testClosures) {
			String testName = namedClosure.name;
			Closure closure = namedClosure.closure;

			ITest test = getNewTestInstance(testName);
			test.setup(config, getHomeDirectory());
			closure.setDelegate(test);
			closure.setResolveStrategy(Closure.DELEGATE_ONLY);
			closure.call();
			addTest(test);
		}
	}

	protected abstract ITest getNewTestInstance(String name);

	public void setScript(String script) {
		this.script = script;
	}

	public void name(String name) {
		this.name = name;
	}

	public void profile(String profile) {
		this.profiles.add(profile);
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

	public void addProfile(String profile) {
		this.profiles.add(profile);
	}

	public List<String> getProfiles() {
		return profiles;
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

	public File getHomeDirectory() {
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

	private void addTest(ITest test) {
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

	@Override
	public void setFailedTests(boolean failedTests) {
		this.failedTests = failedTests;
	}

	@Override
	public boolean hasFailedTests() {
		return failedTests;
	}

	protected String makeAbsolute(String path) {
		return new File(directory, path).getAbsolutePath();
	}

	protected boolean isRelative(String path) {
		return path.startsWith("../") || path.startsWith("./");
	}

	@Override
	public String toString() {
		return name;
	}

	class NamedClosure {
		public String name;
		public Closure closure;

		public NamedClosure(String name, Closure closure) {
			this.name = name;
			this.closure = closure;
		}
	}

}
