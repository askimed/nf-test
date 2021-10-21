package com.github.lukfor.testflight.core;

import java.util.List;

public interface ITestSuite {

	public List<ITest> getTests();

	public String getName();
	
	public void setProfile(String profile);

}