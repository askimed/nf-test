
package com.github.lukfor.testflight.core;

import com.github.lukfor.testflight.util.AnsiColors;
import com.github.lukfor.testflight.util.AnsiText;

public class AnsiTestExecutionListener implements ITestExecutionListener {

	private int count = 0;

	private int failed = 0;

	private long start = 0;

	private long end = 0;

	public static final int TEST_PADDING = 2;

	@Override
	public void testPlanExecutionStarted() {

		start = System.currentTimeMillis();

	}

	@Override
	public void testPlanExecutionFinished() {

		end = System.currentTimeMillis();

		double executionTime = ((end - start) / 1000.0);

		System.out.println();
		System.out.println();

		if (failed > 0) {
			System.out.print(AnsiText.bold(AnsiColors.red("FAILURE: ")));
		} else {
			System.out.print(AnsiText.bold(AnsiColors.green("SUCCESS: ")));
		}

		System.out.print("Executed " + count + " tests in " + executionTime + "s");

		if (failed > 0) {
			System.out.print(" (" + failed + " failed)");
		}
		System.out.println();
		System.out.println();
		
	}

	@Override
	public void testSuiteExecutionStarted(ITestSuite testSuite) {
		
		System.out.println();
		System.out.println(AnsiText.bold(testSuite.getName()));
		System.out.println();

	}

	@Override
	public void testSuiteExecutionFinished(ITestSuite testSuite) {

	}

	@Override
	public void executionSkipped(ITest test, String reason) {

	}

	@Override
	public void executionStarted(ITest test) {

		count++;
		System.out.print(AnsiText.padding(AnsiText.bold("Test") + " '" + test.getName() + "' ", TEST_PADDING));

	}

	@Override
	public void executionFinished(ITest test, TestExecutionResult result) {

		switch (result.getStatus()) {

		case PASSED:
			System.out.println(AnsiColors.green("PASSED") + " (" + result.getExecutionTimeInSecs() + "s)");
			break;

		case ABORTED:
			System.out.println(AnsiColors.red("ABORTED") + " (" + result.getExecutionTimeInSecs() + "s)");
			break;

		case FAILED:
			failed++;
			System.out.println(AnsiColors.red("FAILED") + " (" + result.getExecutionTimeInSecs() + "s)");
			System.out.println();
			System.out.println(AnsiText.padding(AnsiColors.red(result.getThrowable().toString()), TEST_PADDING));
			System.out.println();
			break;

		}

	}

	public int getFailed() {
		return failed;
	}

	public int getCount() {
		return count;
	}

}
