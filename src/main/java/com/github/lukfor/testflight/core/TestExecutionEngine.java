package com.github.lukfor.testflight.core;

import java.util.List;
import java.util.Vector;

import com.github.lukfor.testflight.lang.TestSuiteBuilder;
import com.github.lukfor.testflight.util.AnsiColors;

public class TestExecutionEngine {

	private String[] scripts;

	private ITestExecutionListener listener = new AnsiTestExecutionListener();

	public void setScripts(String... scripts) {
		this.scripts = scripts;
	}

	protected List<ITestSuite> parse() throws Exception {

		List<ITestSuite> testSuits = new Vector<ITestSuite>();

		for (String script : scripts) {
			ITestSuite testSuite = TestSuiteBuilder.parse(script);
			testSuits.add(testSuite);
		}

		return testSuits;

	}

	public int execute() throws Throwable {

		List<ITestSuite> testSuits = parse();

		if (testSuits.size() == 0) {
			System.out.println(AnsiColors.red("Error: no valid tests found."));
			System.out.println();
			return 1;
		}

		listener.testPlanExecutionStarted();

		for (ITestSuite testSuite : testSuits) {

			listener.testSuiteExecutionStarted(testSuite);

			for (ITest test : testSuite.getTests()) {
				listener.executionStarted(test);
				TestExecutionResult result = new TestExecutionResult();

				try {

					result.setStartTime(System.currentTimeMillis());
					test.execute();
					result.setStatus(TestExecutionResultStatus.PASSED);

				} catch (Throwable e) {

					result.setStatus(TestExecutionResultStatus.FAILED);
					result.setThrowable(e);

				}
				result.setEndTime(System.currentTimeMillis());
				listener.executionFinished(test, result);
				
			}

			listener.testSuiteExecutionFinished(testSuite);

		}

		listener.testPlanExecutionFinished();

		if (listener.getFailed() > 0) {
			return 1;
		} else {
			return 0;
		}

	}

}
