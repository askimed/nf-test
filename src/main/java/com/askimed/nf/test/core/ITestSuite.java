package com.askimed.nf.test.core;

import java.io.File;
import java.util.List;

import com.askimed.nf.test.config.Config;

public interface ITestSuite extends ITaggable {

	public List<ITest> getTests();

	public String getName();

	public void setProfile(String profile);

	public void setGlobalConfigFile(File config);

	public void setFilename(String filename);

	public String getFilename();

	public void configure(Config config);

}