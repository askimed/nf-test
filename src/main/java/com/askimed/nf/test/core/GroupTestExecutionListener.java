package com.askimed.nf.test.core;

import java.util.List;
import java.util.Vector;

public class GroupTestExecutionListener implements ITestExecutionListener {

	private List<ITestExecutionListener> listeners = new Vector<ITestExecutionListener>();

	@Override
	public synchronized void testPlanExecutionStarted() {
		for (ITestExecutionListener listener : listeners) {
			listener.testPlanExecutionStarted();
		}
	}

	@Override
	public synchronized void testPlanExecutionFinished() throws Exception {
		for (ITestExecutionListener listener : listeners) {
			listener.testPlanExecutionFinished();
		}
	}

	@Override
	public synchronized void testSuiteExecutionStarted(ITestSuite testSuite) {
		for (ITestExecutionListener listener : listeners) {
			listener.testSuiteExecutionStarted(testSuite);
		}
	}

	@Override
	public synchronized void testSuiteExecutionFinished(ITestSuite testSuite) {
		for (ITestExecutionListener listener : listeners) {
			listener.testSuiteExecutionFinished(testSuite);
		}
	}

	@Override
	public synchronized void executionSkipped(ITest test, String reason) {
		for (ITestExecutionListener listener : listeners) {
			listener.executionSkipped(test, reason);
		}
	}

	@Override
	public synchronized void executionStarted(ITest test) {
		for (ITestExecutionListener listener : listeners) {
			listener.executionStarted(test);
		}
	}

	@Override
	public synchronized void executionFinished(ITest test, TestExecutionResult testExecutionResult) {
		for (ITestExecutionListener listener : listeners) {
			listener.executionFinished(test, testExecutionResult);
		}
	}

	@Override
	public synchronized void setDebug(boolean debug) {
		for (ITestExecutionListener listener : listeners) {
			listener.setDebug(debug);
		}
	}

	public synchronized void addListener(ITestExecutionListener listener) {
		listeners.add(listener);
	}

	public synchronized void removeListener(ITestExecutionListener listener) {
		listeners.remove(listener);
	}

}
