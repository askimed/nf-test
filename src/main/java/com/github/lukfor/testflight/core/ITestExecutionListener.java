package com.github.lukfor.testflight.core;

import com.github.lukfor.testflight.lang.NextflowTest;
import com.github.lukfor.testflight.lang.NextflowTestSuite;

public interface ITestExecutionListener {

	public void testPlanExecutionStarted();

	public void testPlanExecutionFinished();

	public void testSuiteExecutionStarted(NextflowTestSuite testSuite);

	public void testSuiteExecutionFinished(NextflowTestSuite testSuite);

	public int getFailed();

	public void executionSkipped(NextflowTest test, String reason);

	public void executionStarted(NextflowTest test);

	public void executionFinished(NextflowTest test, TestExecutionResult testExecutionResult);

}
