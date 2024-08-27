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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

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
			System.out.println("Warning: every snapshot that fails during this test run is re-record.");
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

		AtomicInteger totalTests = new AtomicInteger(0);
		AtomicInteger failedTests = new AtomicInteger(0);
	
		log.info("Started test plan");

		listener.testPlanExecutionStarted();

		AtomicBoolean failed = new AtomicBoolean(false);
		ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		try {
			List<Future<Boolean>> suiteFutures = new Vector<>();
	
			for (ITestSuite testSuite : testSuits) {
				System.out.println("Test suite: " + testSuite);
				Future<Boolean> suiteFuture = executorService.submit(() -> {
					for (String profile : profiles) {
						testSuite.addProfile(profile);
					}
	
					if (configFile != null) {
						testSuite.setGlobalConfigFile(configFile);
					}
	
					log.info("Running testsuite '{}' from file '{}'.", testSuite, testSuite.getFilename());
					System.out.println("Running testsuite '" + testSuite + "' from file '" + testSuite.getFilename() + "'.");
					

					try {	
						listener.testSuiteExecutionStarted(testSuite);
					} catch (Throwable e) {
						throw new RuntimeException("Error starting test suite", e);
					}
					System.out.println("After staring execution");
	
					List<Future<TestExecutionResult>> futures = new Vector<>();
					
					System.out.println("Test suite tests: " + testSuite.getTests().size());
					for (ITest test : testSuite.getTests()) {
						totalTests.incrementAndGet();
						System.out.println("Total tests: " + totalTests.get());
						Future<TestExecutionResult> future = executorService.submit(() -> {
							TestExecutionResult result = new TestExecutionResult(test);
							if (test.isSkipped()) {
								log.info("Test '{}' skipped.", test);
								listener.executionSkipped(test, "");
								return result;
							}
	
							log.info("Run test '{}'. type: {}", test, test.getClass().getName());
							System.out.println("Run test '" + test + "'. type: " + test.getClass().getName());
							try {
								testSuite.setupTest(test);
							} catch (Throwable e) {
								throw new RuntimeException("Error setting up test", e);
							}
	
							listener.executionStarted(test);
							test.setWithTrace(withTrace);
							test.setUpdateSnapshot(updateSnapshot);
							test.setCIMode(ciMode);
							result.setStartTime(System.currentTimeMillis());
	
							try {
								if (debug) {
									test.setDebug(true);
								}
	
								if (!dryRun) {
									test.execute();
								}
								result.setStatus(TestExecutionResultStatus.PASSED);
								System.out.println(AnsiColors.green("Test passed"));
							} catch (Throwable e) {
								System.out.println(AnsiColors.red("Test failed: " + e.getMessage()));
								result.setStatus(TestExecutionResultStatus.FAILED);
								result.setThrowable(e);
								try {
									result.setErrorReport(test.getErrorReport());
								} catch (Throwable e1) {
									throw new RuntimeException("Error getting error report", e1);
								}
								testSuite.setFailedTests(true);
								synchronized (this) {
									failedTests.incrementAndGet();
								}
							}
	
							try {
								test.cleanup();
							} catch (Throwable e) {
								System.out.println("Error cleaning up test: " + e.getMessage());
								throw new RuntimeException("Error cleaning up test", e);
							}
							result.setEndTime(System.currentTimeMillis());
	
							log.info("Test '{}' finished. status: {}", result.getTest(), result.getStatus(), result.getThrowable());
	
							listener.executionFinished(test, result);
							System.out.println("FINISHED MAN!");
							return result;
						});
						futures.add(future);
					}

					for (Future<TestExecutionResult> future : futures) {
						try {
							TestExecutionResult result = future.get();
							if (result.getStatus() == TestExecutionResultStatus.FAILED) {
								System.out.println("Test failed: " + result.getThrowable().getMessage());
								failed.set(true);
							}
						} catch (Exception e) {
							log.error("Error while executing test", e);
							System.out.println("Error while executing test: " + e);
							failed.set(true);
						}
						catch (Throwable e) {
							log.error("Throwable while executing test", e);
							System.out.println("Throwable while executing test: ");
							e.printStackTrace();
							failed.set(true);
						}
					}
	
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
	
					return true;
				});
				suiteFutures.add(suiteFuture);
			}
	
			for (Future<Boolean> suiteFuture : suiteFutures) {
				try {
					if (!suiteFuture.get()) {
						// System.out.println("Test suite failed");
						failed.set(true);
					}
				} catch (Exception e) {
					log.error("Error while executing test suite", e);
					// System.out.println("Error while executing test suite: " + e);
					failed.set(true);
				}
			}
	
		} catch (Throwable e) {
			log.error("Error in the full try", e);
			// System.out.println("Error in the full try: " + e.getMessage());
			failed.set(true);
		} finally {
			// System.out.println("Something else happened");
			executorService.shutdown();
		}
	
		log.info("Executed {} tests. {} tests failed. Done!", totalTests.get(), failedTests.get());
	
		listener.testPlanExecutionFinished();
	
		return (failed.get()) ? 1 : 0;
	}

	public void setTestSuites(List<ITestSuite> testSuits) {
		this.testSuits = testSuits;
	}
}
