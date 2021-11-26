package com.askimed.nf.test.core;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.lang.TestSuiteBuilder;
import com.askimed.nf.test.util.AnsiColors;
import com.askimed.nf.test.util.FileUtil;

public class TestExecutionEngine {

	private List<File> scripts;

	private ITestExecutionListener listener = new AnsiTestExecutionListener();

	private boolean debug = false;

	private String profile = null;

	private File workDir = null;

	private File configFile = null;

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

	protected List<ITestSuite> parse() throws Exception {

		List<ITestSuite> testSuits = new Vector<ITestSuite>();

		for (File script : scripts) {
			if (!script.exists()) {
				throw new Exception("Test file '" + script.getAbsolutePath() + "' not found.");
			}
			ITestSuite testSuite = TestSuiteBuilder.parse(script);
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

		//cleanup
		
		FileUtil.deleteDirectory(workDir);
		FileUtil.createDirectory(workDir);
		
		listener.testPlanExecutionStarted();

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
				listener.executionStarted(test);
				TestExecutionResult result = new TestExecutionResult();
				test.setup(workDir);
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

				}
				test.cleanup();
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
