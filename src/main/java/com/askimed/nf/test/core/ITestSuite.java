package com.askimed.nf.test.core;

import java.io.File;
import java.util.List;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.lang.extensions.SnapshotFile;

public interface ITestSuite extends ITaggable {

	public List<ITest> getTests();

	public String getName();

	public void setProfile(String profile);

	public void setGlobalConfigFile(File config);

	public void setFilename(String filename);

	public String getFilename();
	
	public String getDirectory();

	public void configure(Config config);
	
	public boolean hasSkippedTests();
	
	public void setFailedTests(boolean b);
	
	public boolean hasFailedTests();
	
	public SnapshotFile getSnapshot();
	
	public boolean hasSnapshotLoaded();
	
	public void evalualteTestClosures() throws Throwable;

}