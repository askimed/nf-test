package com.askimed.nf.test.core;

import java.io.File;

public interface ITest {

	public void setup(File baseDir) throws Throwable;

	public void execute() throws Throwable;

	public void cleanup() throws Throwable;

	public String getErrorReport() throws Throwable;

	public String getName();

	public void setDebug(boolean debug);

	public String getHash();

}
