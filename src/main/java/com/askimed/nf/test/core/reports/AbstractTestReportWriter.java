
package com.askimed.nf.test.core.reports;

import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.core.ITestExecutionListener;
import com.askimed.nf.test.core.ITestSuite;
import com.askimed.nf.test.core.TestExecutionResult;
import com.askimed.nf.test.core.TestSuiteExecutionResult;

public abstract class AbstractTestReportWriter implements ITestExecutionListener {

    // Using Vector for thread-safe operations
    private List<TestSuiteExecutionResult> testSuites = new Vector<>();

    private int count = 0;

    private int failed = 0;

    private int skipped = 0;
	

    private long startTime;
    private long endTime;

    @Override
    public synchronized void testPlanExecutionStarted() {
        startTime = System.currentTimeMillis();
    }

    @Override
    public synchronized void testPlanExecutionFinished() throws Exception {
        endTime = System.currentTimeMillis();
        writeToFile(testSuites);
    }

    @Override
    public synchronized void testSuiteExecutionStarted(ITestSuite testSuite) {
        TestSuiteExecutionResult suiteResult = new TestSuiteExecutionResult(testSuite);
        suiteResult.setStartTime(System.currentTimeMillis());
        testSuites.add(suiteResult);
    }

    @Override
    public synchronized void testSuiteExecutionFinished(ITestSuite testSuite) {
        for (TestSuiteExecutionResult suiteResult : testSuites) {
            if (suiteResult.getTestSuite().equals(testSuite)) {
                suiteResult.setEndTime(System.currentTimeMillis());
                count += suiteResult.getTests().size();
                failed += suiteResult.getFailed();
                skipped += suiteResult.getSkipped();
                break;
            }
        }
    }

    @Override
    public synchronized void executionSkipped(ITest test, String reason) {
        // TODO: create TestExectionResult and handle skipped cases
    }

    @Override
    public synchronized void executionStarted(ITest test) {
        // No specific action needed at the start of a test
    }

    @Override
    public synchronized void executionFinished(ITest test, TestExecutionResult result) {
        for (TestSuiteExecutionResult suiteResult : testSuites) {
            if (suiteResult.getTestSuite().equals(test.getTestSuite())) {
                suiteResult.addTestExecutionResult(result);
                break;
            }
        }
    }

    public synchronized int getFailed() {
        return failed;
    }

    public synchronized int getSkipped() {
        return skipped;
    }

    public synchronized int getCount() {
        return count;
    }

    public synchronized long getEndTime() {
        return endTime;
    }

    public synchronized long getStartTime() {
        return startTime;
    }

    public synchronized double getExecutionTimeInSecs() {
        return (endTime - startTime) / 1000.0;
    }

    @Override
    public synchronized void setDebug(boolean debug) {
        // No debug functionality in this base class
    }

    abstract public void writeToFile(List<TestSuiteExecutionResult> testSuites) throws Exception;

}
