package com.askimed.nf.test.core;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.askimed.nf.test.lang.TestSuiteBuilder;
import com.askimed.nf.test.lang.extensions.SnapshotFile;
import com.askimed.nf.test.plugins.PluginManager;
import com.askimed.nf.test.util.AnsiColors;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.FileUtil;
import com.askimed.nf.test.util.OutputFormat;
import com.github.javaparser.utils.Log;

import groovy.json.JsonOutput;

public class TestExecutionEngine {

	private List<ITestSuite> testSuits;

	private ITestExecutionListener listener = new AnsiTestExecutionListener();

	private boolean debug = false;

	private List<String> profiles = new Vector<String>();

	private File configFile = null;

	private File baseDir = new File(System.getProperty("user.dir"));

	private boolean withTrace = true;

	private boolean updateSnapshot = false;

	private boolean ciMode = false;

	private boolean cleanSnapshot = false;

	private boolean dryRun = false;

	private static Logger log = LoggerFactory.getLogger(TestExecutionEngine.class);

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void addProfile(String profile) {
		if (profile == null) {
			return;
		}
		this.profiles.add(profile);
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	public void setWithTrace(boolean withTrace) {
		if (withTrace == false) {
			System.out.println("Warning: Tracing is disabled. `workflow.trace` is not supported.");
		}
		this.withTrace = withTrace;
	}

	public void setUpdateSnapshot(boolean updateSnapshot) {
		if (updateSnapshot) {
			System.out.println("Warning: every snapshot that fails during this test run is re-recorded.");
		}
		this.updateSnapshot = updateSnapshot;
	}

	public void setCIMode(boolean ciMode) {
		if (ciMode) {
			System.out.println("nf-test runs in CI mode.");
		}
		this.ciMode = ciMode;
	}

	public void setCleanSnapshot(boolean cleanSnapshot) {
		this.cleanSnapshot = cleanSnapshot;
	}

	public void setDryRun(boolean dryRun) {
		this.dryRun = dryRun;
	}

	public void setListener(ITestExecutionListener listener) {
		this.listener = listener;
	}

	public int execute() throws Throwable {

		if (configFile != null) {
			if (!configFile.exists()) {
				System.out.println(
						AnsiColors.red("Error: Test config file '" + configFile.getAbsolutePath() + "'not found"));
				System.out.println();
				return 1;
			}
		}

		if (testSuits.isEmpty()) {
			System.out.println(AnsiColors.red("Error: no valid tests found."));
			System.out.println();
			return 1;
		}

		listener.setDebug(debug);

		int totalTests = 0;
		int failedTests = 0;

		log.info("Started test plan");

		listener.testPlanExecutionStarted();

		boolean failed = false;
		for (ITestSuite testSuite : testSuits) {

			for (String profile : profiles) {
				testSuite.addProfile(profile);
			}

			if (configFile != null) {
				// TODO: addConfig as list
				testSuite.setGlobalConfigFile(configFile);
			}

			log.info("Running testsuite '{}' from file '{}'.", testSuite, testSuite.getFilename());

			listener.testSuiteExecutionStarted(testSuite);

			for (ITest test : testSuite.getTests()) {
				if (test.isSkipped()) {
					log.info("Test '{}' skipped.", test);
					listener.executionSkipped(test, "");
					continue;
				}

				log.info("Run test '{}'. type: {}", test, test.getClass().getName());
				totalTests++;

				testSuite.setupTest(test);

				listener.executionStarted(test);
				TestExecutionResult result = new TestExecutionResult(test);
				test.setWithTrace(withTrace);
				test.setUpdateSnapshot(updateSnapshot);
				test.setCIMode(ciMode);
				try {

					// override debug flag from CLI
					if (debug) {
						test.setDebug(true);
					}

					result.setStartTime(System.currentTimeMillis());
					if (!dryRun) {
						test.execute();
					}
					result.setStatus(TestExecutionResultStatus.PASSED);

				} catch (Throwable e) {

					result.setStatus(TestExecutionResultStatus.FAILED);
					result.setThrowable(e);
					result.setErrorReport(test.getErrorReport());
					failed = true;
					testSuite.setFailedTests(true);
					failedTests++;

				}
				test.cleanup();
				result.setEndTime(System.currentTimeMillis());

				log.info("Test '{}' finished. status: {}", result.getTest(), result.getStatus(), result.getThrowable());

				listener.executionFinished(test, result);

			}

			// Remove obsolete snapshots when no test was skipped and no test failed.
			if (cleanSnapshot && !testSuite.hasSkippedTests() && !testSuite.hasFailedTests()
					&& testSuite.hasSnapshotLoaded()) {
				log.info("Clean up obsolete snapshots");
				SnapshotFile snapshot = testSuite.getSnapshot();
				snapshot.removeObsoleteSnapshots();
				snapshot.save();
			}

			log.info("Testsuite '{}' finished. snapshot file: {}, skipped tests: {}, failed tests: {}", testSuite,
					testSuite.hasSnapshotLoaded(), testSuite.hasSkippedTests(), testSuite.hasFailedTests());

			listener.testSuiteExecutionFinished(testSuite);

		}

		log.info("Executed {} tests. {} tests failed. Done!", totalTests, failedTests);

		listener.testPlanExecutionFinished();

		return (failed) ? 1 : 0;

	}

	public void setTestSuites(List<ITestSuite> testSuits) {
		this.testSuits = testSuits;
	}
}
