package com.askimed.nf.test.core;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import javax.xml.stream.XMLStreamException;

public class GroupTestExecutionListener implements ITestExecutionListener {

	private List<ITestExecutionListener> listeners = new Vector<ITestExecutionListener>();

	@Override
	public void testPlanExecutionStarted() {
		for (ITestExecutionListener listener : listeners) {
			listener.testPlanExecutionStarted();
		}
	}

	@Override
	public void testPlanExecutionFinished() throws IOException, XMLStreamException {
		for (ITestExecutionListener listener : listeners) {
			listener.testPlanExecutionFinished();
		}
	}

	@Override
	public void testSuiteExecutionStarted(ITestSuite testSuite) {
		for (ITestExecutionListener listener : listeners) {
			listener.testSuiteExecutionStarted(testSuite);
		}
	}

	@Override
	public void testSuiteExecutionFinished(ITestSuite testSuite) {
		for (ITestExecutionListener listener : listeners) {
			listener.testSuiteExecutionFinished(testSuite);
		}
	}

	@Override
	public void executionSkipped(ITest test, String reason) {
		for (ITestExecutionListener listener : listeners) {
			listener.executionSkipped(test, reason);
		}
	}

	@Override
	public void executionStarted(ITest test) {
		for (ITestExecutionListener listener : listeners) {
			listener.executionStarted(test);
		}
	}

	@Override
	public void executionFinished(ITest test, TestExecutionResult testExecutionResult) {
		for (ITestExecutionListener listener : listeners) {
			listener.executionFinished(test, testExecutionResult);
		}
	}

	@Override
	public void setDebug(boolean debug) {
		for (ITestExecutionListener listener : listeners) {
			listener.setDebug(debug);
		}
	}

	public void addListener(ITestExecutionListener listener) {
		listeners.add(listener);
	}

	public void removeListener(ITestExecutionListener listener) {
		listeners.remove(listener);
	}

}
