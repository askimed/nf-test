package com.github.lukfor.testflight.core;

import java.util.List;
import java.util.Vector;

import com.github.lukfor.testflight.lang.NextflowTestSuiteBuilder;
import com.github.lukfor.testflight.util.AnsiColors;
import com.github.lukfor.testflight.lang.NextflowTest;
import com.github.lukfor.testflight.lang.NextflowTestSuite;

public class TestExecutionEngine {

	private String[] scripts;

	private ITestExecutionListener listener = new AnsiTestExecutionListener();

	public void setScripts(String... scripts) {
		this.scripts = scripts;
	}

	protected List<NextflowTestSuite> parse() throws Exception {

		List<NextflowTestSuite> testSuits = new Vector<NextflowTestSuite>();

		for (String script : scripts) {
			NextflowTestSuite testSuite = NextflowTestSuiteBuilder.parse(script);
			testSuits.add(testSuite);
		}

		return testSuits;

	}

	public int execute() throws Throwable {

		List<NextflowTestSuite> testSuits = parse();

		if (testSuits.size() == 0) {
			System.out.println(AnsiColors.red("Error: no valid tests found."));
			System.out.println();
			return 1;
		}

		listener.testPlanExecutionStarted();

		for (NextflowTestSuite testSuite : testSuits) {

			listener.testSuiteExecutionStarted(testSuite);

			for (NextflowTest test : testSuite.getTests()) {
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
