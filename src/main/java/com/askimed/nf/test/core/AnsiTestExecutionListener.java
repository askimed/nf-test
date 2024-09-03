
package com.askimed.nf.test.core;

import com.askimed.nf.test.lang.extensions.SnapshotFile;
import com.askimed.nf.test.util.AnsiColors;
import com.askimed.nf.test.util.AnsiText;

public class AnsiTestExecutionListener implements ITestExecutionListener {

	private int count = 0;

	private int failed = 0;

	private long start = 0;

	private long end = 0;

	private boolean debug = false;

	private int updatedSnapshots;

	private int createdSnapshots;

	private int obsoleteSnapshots;

	public static final int TEST_PADDING = 2;

	@Override
	public synchronized void testPlanExecutionStarted() {

		start = System.currentTimeMillis();

	}

	@Override
	public synchronized void testPlanExecutionFinished() {

		end = System.currentTimeMillis();

		double executionTime = ((end - start) / 1000.0);

		System.out.println();

		if ((updatedSnapshots + createdSnapshots + obsoleteSnapshots) > 0) {
			System.out.println();
			System.out.println("Snapshot Summary:");
		}
		if (updatedSnapshots > 0) {
			System.out.println(AnsiText.padding(updatedSnapshots + " updated", TEST_PADDING));
		}
		if (createdSnapshots > 0) {
			System.out.println(AnsiText.padding(createdSnapshots + " created", TEST_PADDING));
		}

		if (obsoleteSnapshots > 0) {
			System.out.println(AnsiColors.yellow(AnsiText.padding(obsoleteSnapshots + " obsolete", TEST_PADDING)));
		}

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
	public synchronized void testSuiteExecutionStarted(ITestSuite testSuite) {

		System.out.println();
		System.out.println(AnsiText.bold(testSuite.getName()));
		System.out.println();

	}

	@Override
	public synchronized void testSuiteExecutionFinished(ITestSuite testSuite) {

		if (!testSuite.hasSnapshotLoaded()) {
			return;
		}

		SnapshotFile snapshot = testSuite.getSnapshot();
		if ((snapshot.getUpdatedSnapshots().size() + snapshot.getCreatedSnapshots().size()
				+ snapshot.getObsoleteSnapshots().size()) > 0 || testSuite.hasSkippedTests()) {
			System.out.println(AnsiText.padding("Snapshots:", TEST_PADDING));
		}
		if (snapshot.getUpdatedSnapshots().size() > 0) {
			System.out.println(AnsiText.padding(
					snapshot.getUpdatedSnapshots().size() + " updated " + snapshot.getUpdatedSnapshots(),
					2 * TEST_PADDING));
		}
		if (snapshot.getCreatedSnapshots().size() > 0) {
			System.out.println(AnsiText.padding(
					snapshot.getCreatedSnapshots().size() + " created " + snapshot.getCreatedSnapshots(),
					2 * TEST_PADDING));
		}

		updatedSnapshots += snapshot.getUpdatedSnapshots().size();
		createdSnapshots += snapshot.getCreatedSnapshots().size();

		// if we have at least one skipped test, we can not
		// determine if a snapshot is obsolete.
		if (testSuite.hasSkippedTests() || testSuite.hasFailedTests()) {
			System.out.println(AnsiText.padding(
					"Obsolete snapshots can only be checked if all tests of a file are executed successful.",
					2 * TEST_PADDING));
			return;
		}

		if (snapshot.getObsoleteSnapshots().size() > 0) {
			System.out.println(AnsiColors.yellow(AnsiText.padding(
					snapshot.getObsoleteSnapshots().size() + " obsolete " + snapshot.getObsoleteSnapshots(),
					2 * TEST_PADDING)));
		}

		obsoleteSnapshots += snapshot.getObsoleteSnapshots().size();

	}

	@Override
	public synchronized void executionSkipped(ITest test, String reason) {

	}

	@Override
	public void executionStarted(ITest test) {

		count++;
		System.out.print(AnsiText.padding(
				AnsiText.bold("Test") + " [" + test.getHash().substring(0, 8) + "]" + " '" + test.getName() + "' ",
				TEST_PADDING));

	}

	@Override
	public synchronized void executionFinished(ITest test, TestExecutionResult result) {

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
			if (debug) {
				result.getThrowable().printStackTrace();
			}
			if (result.getErrorReport() != null) {
				System.out.println(AnsiText.padding(AnsiColors.red(result.getErrorReport()), TEST_PADDING));
			}
			System.out.println();
			break;

		}

	}

	public synchronized int getCount() {
		return count;
	}

	@Override
	public synchronized void setDebug(boolean debug) {
		this.debug = debug;
	}

}
