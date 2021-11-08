package com.askimed.nf.test.core;

public interface ITest {

	public void setup() throws Throwable;

	public void execute() throws Throwable;

	public void cleanup() throws Throwable;

	public String getErrorReport() throws Throwable;
	
	public String getName();

	public void setDebug(boolean debug);

}
