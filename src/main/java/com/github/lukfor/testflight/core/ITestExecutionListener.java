package com.github.lukfor.testflight.core;

public interface ITestExecutionListener {

	public void testPlanExecutionStarted();

	public void testPlanExecutionFinished();

	public void testSuiteExecutionStarted(ITestSuite testSuite);

	public void testSuiteExecutionFinished(ITestSuite testSuite);

	public int getFailed();

	public void executionSkipped(ITest test, String reason);

	public void executionStarted(ITest test);

	public void executionFinished(ITest test, TestExecutionResult testExecutionResult);

}
