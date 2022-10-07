package com.askimed.nf.test.core;

public interface ITestExecutionListener {

	public void testPlanExecutionStarted();

	public void testPlanExecutionFinished();

	public void testSuiteExecutionStarted(ITestSuite testSuite);

	public void testSuiteExecutionFinished(ITestSuite testSuite);

	public void executionSkipped(ITest test, String reason);

	public void executionStarted(ITest test);

	public void executionFinished(ITest test, TestExecutionResult testExecutionResult);

	public void setDebug(boolean debug);

}
