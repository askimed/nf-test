package com.askimed.nf.test.core;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

public interface ITestExecutionListener {

	public void testPlanExecutionStarted();

	public void testPlanExecutionFinished() throws IOException, XMLStreamException;

	public void testSuiteExecutionStarted(ITestSuite testSuite);

	public void testSuiteExecutionFinished(ITestSuite testSuite);

	public void executionSkipped(ITest test, String reason);

	public void executionStarted(ITest test);

	public void executionFinished(ITest test, TestExecutionResult testExecutionResult);

	public void setDebug(boolean debug);

}
