package com.github.lukfor.nf.test.core;

import java.io.File;
import java.util.List;

public interface ITestSuite {

	public List<ITest> getTests();

	public String getName();
	
	public void setProfile(String profile);
	
	public void setConfigFile(File config);

}