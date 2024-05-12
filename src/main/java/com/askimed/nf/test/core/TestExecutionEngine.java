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

	private List<File> scripts;

	private ITestExecutionListener listener = new AnsiTestExecutionListener();

	private boolean debug = false;

	private List<String> profiles = new Vector<String>();

	private File configFile = null;

	private File baseDir = new File(System.getProperty("user.dir"));

	private boolean withTrace = true;

	private boolean updateSnapshot = false;

	private boolean cleanSnapshot = false;

	private String libDir = "";

	private PluginManager pluginManager = null;

	private TagQuery tagQuery = new TagQuery();

	private static Logger log = LoggerFactory.getLogger(TestExecutionEngine.class);

	public void setScripts(List<File> scripts) {
		this.scripts = scripts;
	}

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
			System.out.println("Warning: every snapshot that fails during this test run is re-record.");
		}
		this.updateSnapshot = updateSnapshot;
	}

	public void setCleanSnapshot(boolean cleanSnapshot) {
		this.cleanSnapshot = cleanSnapshot;
	}

	public void setTagQuery(TagQuery tagQuery) {
		this.tagQuery = tagQuery;
	}

	public void setLibDir(String libDir) {
		this.libDir = libDir;
	}

	public void setListener(ITestExecutionListener listener) {
		this.listener = listener;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}

	protected List<ITestSuite> parse(TagQuery tagQuery) throws Throwable {

		List<ITestSuite> testSuits = new Vector<ITestSuite>();

		for (File script : scripts) {
			String testId = null;
			if (script.getAbsolutePath().contains("@")) {
				String[] tiles = script.getAbsolutePath().split("@");
				script = new File(tiles[0]);
				testId = tiles[1];
			}
			if (!script.exists()) {
				throw new Exception("Test file '" + script.getAbsolutePath() + "' not found.");
			}
			ITestSuite testSuite = TestSuiteBuilder.parse(script, libDir, pluginManager);

			boolean empty = true;

			for (ITest test : testSuite.getTests()) {
				if (testId != null) {
					if (!test.getHash().startsWith(testId)) {
						test.skip();
					}
				}

				if (!tagQuery.matches(test)) {
					test.skip();
				}

				if (!test.isSkipped()) {
					empty = false;
				}

			}

			if (!empty) {
				testSuits.add(testSuite);
			}
		}

		return testSuits;

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

		List<ITestSuite> testSuits = parse(tagQuery);

		if (testSuits.size() == 0) {
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
				try {

					// override debug flag from CLI
					if (debug) {
						test.setDebug(true);
					}

					result.setStartTime(System.currentTimeMillis());
					test.execute();
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

	public int listTests(OutputFormat format) throws Throwable {

		if (configFile != null) {
			if (!configFile.exists()) {
				System.out.println(
						AnsiColors.red("Error: Test config file '" + configFile.getAbsolutePath() + "'not found"));
				System.out.println();
				return 1;
			}
		}

		List<ITestSuite> testSuits = parse(tagQuery);

		if (testSuits.size() == 0) {
			System.out.println(AnsiColors.red("Error: no valid tests found."));
			System.out.println();
			return 1;
		}

		switch (format) {
		case JSON:
		case json:
			printTestsAsJson(testSuits);
			break;
		case RAW:
		case raw:
			printTestsAsList(testSuits);
			break;
		case CSV:
		case csv:
			printTestsAsCsv(testSuits);
			break;
		default:
			printTestsPretty(testSuits);
			break;
		}

		return 0;

	}

	public int listTags(OutputFormat format) throws Throwable {

		if (configFile != null) {
			if (!configFile.exists()) {
				System.out.println(
						AnsiColors.red("Error: Test config file '" + configFile.getAbsolutePath() + "'not found"));
				System.out.println();
				return 1;
			}
		}

		List<ITestSuite> testSuits = parse(tagQuery);

		if (testSuits.size() == 0) {
			System.out.println(AnsiColors.red("Error: no valid tests found."));
			System.out.println();
			return 1;
		}

		Set<String> tags = new HashSet<String>();
		for (ITestSuite testSuite : testSuits) {
			tags.addAll(testSuite.getTags());
			for (ITest test : testSuite.getTests()) {
				tags.addAll(test.getTags());
			}
		}

		switch (format) {
		case JSON:
		case json:
			printTagsAsJson(tags);
			break;
		case CSV:
		case csv:
			printTagsAsCsv(tags);
			break;
		default:
			printTagsPretty(tags);
			break;
		}

		return 0;

	}

	private void printTestsAsJson(List<ITestSuite> testSuits) {
		List<String> tests = new Vector<String>();
		int index = 0;
		for (ITestSuite testSuite : testSuits) {
			for (ITest test : testSuite.getTests()) {
				tests.add(scripts.get(index).getAbsolutePath() + "@" + test.getHash().substring(0, 8));
			}
			index++;
		}
		System.out.println(JsonOutput.toJson(tests));
	}

	private void printTestsAsList(List<ITestSuite> testSuits) {
		int index = 0;
		for (ITestSuite testSuite : testSuits) {
			for (ITest test : testSuite.getTests()) {
				System.out.println(scripts.get(index).getAbsolutePath() + "@" + test.getHash().substring(0, 8));
			}
			index++;
		}
	}

	private void printTestsAsCsv(List<ITestSuite> testSuits) {
		List<String> tests = new Vector<String>();
		int index = 0;
		for (ITestSuite testSuite : testSuits) {
			for (ITest test : testSuite.getTests()) {
				tests.add(scripts.get(index).getAbsolutePath() + "@" + test.getHash().substring(0, 8));
			}
			index++;
		}
		System.out.println(String.join(",", tests));
	}

	private void printTestsPretty(List<ITestSuite> testSuits) {
		int index = 0;
		int count = 0;
		for (ITestSuite testSuite : testSuits) {

			System.out.println();
			System.out.println("[" + FileUtil.makeRelative(baseDir, scripts.get(index)) + "] "
					+ AnsiText.bold(testSuite.getName()));
			System.out.println();

			for (ITest test : testSuite.getTests()) {
				System.out.println(AnsiText.padding("[" + FileUtil.makeRelative(baseDir, scripts.get(index)) + "@"
						+ test.getHash().substring(0, 8) + "] " + AnsiText.bold(test.getName()), 2));
				count++;

			}
			index++;
		}

		System.out.println();
		System.out.println("Found " + count + " tests.");
		System.out.println();
	}

	private void printTagsAsJson(Set<String> tags) {
		System.out.println(JsonOutput.toJson(tags));
	}

	private void printTagsAsCsv(Set<String> tags) {
		System.out.println(String.join(",", tags));
	}

	private void printTagsPretty(Set<String> tags) {
		for (String tag : tags) {
			System.out.println(tag);
		}
	}

}
