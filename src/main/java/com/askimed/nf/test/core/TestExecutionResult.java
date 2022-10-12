package com.askimed.nf.test.core;

public class TestExecutionResult {

	private ITest test;

	private TestExecutionResultStatus status;

	private Throwable throwable;

	private String errorReport;

	private long startTime;

	private long endTime;

	public TestExecutionResult(ITest test) {
		this.test = test;
	}

	public void setStatus(TestExecutionResultStatus status) {
		this.status = status;
	}

	public TestExecutionResultStatus getStatus() {
		return status;
	}

	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
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

	public void setErrorReport(String errorReport) {
		this.errorReport = errorReport;
	}

	public String getErrorReport() {
		return errorReport;
	}

	public ITest getTest() {
		return test;
	}

}
