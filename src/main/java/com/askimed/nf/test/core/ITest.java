package com.askimed.nf.test.core;

import java.io.File;

import com.askimed.nf.test.config.Config;

public interface ITest extends ITaggable {

	public void setup(Config config, File homeDirectory) throws Throwable;

	public void execute() throws Throwable;

	public void cleanup() throws Throwable;

	public String getErrorReport() throws Throwable;

	public String getName();

	public void skip();

	public boolean isSkipped();

	public void setDebug(boolean debug);

	public String getHash();

	public ITestSuite getTestSuite();

	public void setWithTrace(boolean withTrace);

	public void setUpdateSnapshot(boolean updateSnapshot);

	public boolean isUpdateSnapshot();

}
