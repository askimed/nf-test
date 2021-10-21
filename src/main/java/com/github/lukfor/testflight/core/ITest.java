package com.github.lukfor.testflight.core;

public interface ITest {

	public void execute() throws Throwable;

	public String getName();

	public void setDebug(boolean debug);

}
