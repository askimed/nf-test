
package com.askimed.nf.test.core.reports;

import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.core.ITestExecutionListener;
import com.askimed.nf.test.core.ITestSuite;
import com.askimed.nf.test.core.TestExecutionResult;
import com.askimed.nf.test.core.TestSuiteExecutionResult;

public abstract class AbstractTestReportWriter implements ITestExecutionListener {

	private TestSuiteExecutionResult activeTestSuite;

	private List<TestSuiteExecutionResult> testSuites = new Vector<TestSuiteExecutionResult>();

	private int count = 0;

	private int failed = 0;

	private int skipped = 0;

	private long startTime;

	private long endTime;

	@Override
	public void testPlanExecutionStarted() {
		startTime = System.currentTimeMillis();
	}

	@Override
	public void testPlanExecutionFinished() {
		endTime = System.currentTimeMillis();
		writeToFile(testSuites);
	}

	@Override
	public void testSuiteExecutionStarted(ITestSuite testSuite) {
		activeTestSuite = new TestSuiteExecutionResult(testSuite);
		activeTestSuite.setStartTime(System.currentTimeMillis());
		testSuites.add(activeTestSuite);
	}

	@Override
	public void testSuiteExecutionFinished(ITestSuite testSuite) {
		activeTestSuite.setEndTime(System.currentTimeMillis());
		count += activeTestSuite.getTests().size();
		failed += activeTestSuite.getFailed();
		skipped += activeTestSuite.getSkipped();
	}

	@Override
	public void executionSkipped(ITest test, String reason) {
		// TODO: create TestExectionResult
	}

	@Override
	public void executionStarted(ITest test) {

	}

	@Override
	public void executionFinished(ITest test, TestExecutionResult result) {
		activeTestSuite.addTestExecutionResult(result);
	}

	public int getFailed() {
		return failed;
	}

	public int getSkipped() {
		return skipped;
	}

	public int getCount() {
		return count;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public double getExecutionTimeInSecs() {
		return (endTime - startTime) / 1000.0;
	}

	@Override
	public void setDebug(boolean debug) {

	}

	abstract public void writeToFile(List<TestSuiteExecutionResult> testSuites);

}
