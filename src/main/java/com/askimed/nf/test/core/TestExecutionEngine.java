package com.askimed.nf.test.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.lang.TestSuiteBuilder;
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

		// cleanup

		try {
			FileUtil.deleteDirectory(workDir.getAbsoluteFile());
			FileUtil.createDirectory(workDir);
		} catch (Exception e) {
			throw new IOException("Working Directory '" + workDir.getAbsolutePath() + "' could not be deleted:\n" + e);
		}

		listener.testPlanExecutionStarted();
		boolean failed = false;
		for (ITestSuite testSuite : testSuits) {

			// override profile from CLI
			if (profile != null) {
				testSuite.setProfile(profile);
			}

			if (configFile != null) {
				testSuite.setConfigFile(configFile);
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

		if (failed) {
			return 1;
		} else {
			return 0;
		}

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
