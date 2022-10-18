package com.askimed.nf.test.core;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.lang.TestSuiteBuilder;
import com.askimed.nf.test.lang.extensions.SnapshotFile;
import com.askimed.nf.test.util.AnsiColors;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.FileUtil;

public class TestExecutionEngine {

	private List<File> scripts;

	private ITestExecutionListener listener = new AnsiTestExecutionListener();

	private boolean debug = false;

	private String profile = null;

	private File workDir = null;

	private File configFile = null;

	private File baseDir = new File(System.getProperty("user.dir"));

	private boolean withTrace = true;
	
	private boolean updateSnapshot = false;

	public void setScripts(List<File> scripts) {
		this.scripts = scripts;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public void setConfigFile(File configFile) {
		this.configFile = configFile;
	}

	public void setWorkDir(File workDir) {
		this.workDir = workDir;
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
	
	public void setListener(ITestExecutionListener listener) {
		this.listener = listener;
	}

	protected List<ITestSuite> parse() throws Exception {

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
			ITestSuite testSuite = TestSuiteBuilder.parse(script);
			if (testId != null) {
				for (ITest test : testSuite.getTests()) {
					if (!test.getHash().startsWith(testId)) {
						test.skip();
					}
				}
			}
			testSuits.add(testSuite);
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

		List<ITestSuite> testSuits = parse();

		if (testSuits.size() == 0) {
			System.out.println(AnsiColors.red("Error: no valid tests found."));
			System.out.println();
			return 1;
		}

		listener.setDebug(debug);

		listener.testPlanExecutionStarted();

		boolean failed = false;
		for (ITestSuite testSuite : testSuits) {

			// override profile from CLI
			if (profile != null) {
				testSuite.setProfile(profile);
			}

			if (configFile != null) {
				testSuite.setGlobalConfigFile(configFile);
			}

			listener.testSuiteExecutionStarted(testSuite);

			for (ITest test : testSuite.getTests()) {
				if (test.isSkipped()) {
					listener.executionSkipped(test, "");
					continue;
				}
				listener.executionStarted(test);
				TestExecutionResult result = new TestExecutionResult(test);
				test.setup(workDir);
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

				}
				test.cleanup();
				result.setEndTime(System.currentTimeMillis());
				listener.executionFinished(test, result);

			}

			listener.testSuiteExecutionFinished(testSuite);

		}

		listener.testPlanExecutionFinished();

		return (failed) ? 1 : 0;

	}

	public int listTests() throws Throwable {

		int count = 0;

		if (configFile != null) {
			if (!configFile.exists()) {
				System.out.println(
						AnsiColors.red("Error: Test config file '" + configFile.getAbsolutePath() + "'not found"));
				System.out.println();
				return 1;
			}
		}

		List<ITestSuite> testSuits = parse();

		if (testSuits.size() == 0) {
			System.out.println(AnsiColors.red("Error: no valid tests found."));
			System.out.println();
			return 1;
		}

		int index = 0;
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

		return 0;

	}

}
