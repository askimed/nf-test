package com.askimed.nf.test.core;

import java.util.List;
import java.util.Vector;

public class TestSuiteExecutionResult {

	private int failed;

	private int skipped;

	private int passed;

	private long startTime;

	private long endTime;

	private List<TestExecutionResult> tests = new Vector<TestExecutionResult>();

	private ITestSuite testSuite;

	public TestSuiteExecutionResult(ITestSuite testSuite) {
		this.testSuite = testSuite;
	}

	public ITestSuite getTestSuite() {
		return testSuite;
	}

	public int getFailed() {
		return failed;
	}

	public void setFailed(int failed) {
		this.failed = failed;
	}

	public int getSkipped() {
		return skipped;
	}

	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}

	public int getPassed() {
		return passed;
	}

	public void setPassed(int passed) {
		this.passed = passed;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public double getExecutionTimeInSecs() {
		return (endTime - startTime) / 1000.0;
	}

	public List<TestExecutionResult> getTests() {
		return tests;
	}

	public void addTestExecutionResult(TestExecutionResult result) {
		switch (result.getStatus()) {
		case ABORTED:
			failed++;
			break;
		case FAILED:
			failed++;
			break;
		case PASSED:
			passed++;
			break;
		default:
			break;

		}
		tests.add(result);
	}

}
