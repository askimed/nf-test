package com.askimed.nf.test.core;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.lang.extensions.SnapshotFile;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestExecutionEngineRetryTest {

    // ── stubs ──────────────────────────────────────────────────────────────

    /** Listener that silently absorbs all events. */
    private static class NoopListener implements ITestExecutionListener {
        @Override public void testPlanExecutionStarted() {}
        @Override public void testPlanExecutionFinished() {}
        @Override public void testSuiteExecutionStarted(ITestSuite s) {}
        @Override public void testSuiteExecutionFinished(ITestSuite s) {}
        @Override public void executionSkipped(ITest t, String r) {}
        @Override public void executionStarted(ITest t) {}
        @Override public void executionFinished(ITest t, TestExecutionResult r) {}
        @Override public void setDebug(boolean d) {}
    }

    /**
     * Stub test that throws a network error for the first {@code failTimes} execute()
     * calls and succeeds afterwards. Use {@code networkError=false} to throw a
     * plain (non-retryable) error instead.
     */
    private static class StubTest implements ITest {

        final AtomicInteger executeCount = new AtomicInteger(0);
        final AtomicInteger setupCount  = new AtomicInteger(0);
        private final int failTimes;
        private final boolean networkError;

        StubTest(int failTimes, boolean networkError) {
            this.failTimes    = failTimes;
            this.networkError = networkError;
        }

        @Override
        public void execute() throws Throwable {
            int call = executeCount.incrementAndGet();
            if (call <= failTimes) {
                throw new RuntimeException("Nextflow failed");
            }
        }

        @Override
        public String getErrorReport() {
            // Return a network-pattern string only when that's what the test models.
            return networkError ? "Could not resolve host: raw.githubusercontent.com" : "assertion failed";
        }

        @Override public void setup(Config c) { setupCount.incrementAndGet(); }
        @Override public void defineDirectories(File d) {}
        @Override public void cleanup() {}
        @Override public String getName()  { return "stub-test"; }
        @Override public String getHash()  { return "0000000000000000000000000000000000000000"; }
        @Override public void skip()       {}
        @Override public boolean isSkipped() { return false; }
        @Override public void setDebug(boolean d) {}
        @Override public ITestSuite getTestSuite() { return null; }
        @Override public void setWithTrace(boolean w) {}
        @Override public void setUpdateSnapshot(boolean u) {}
        @Override public boolean isUpdateSnapshot() { return false; }
        @Override public void setCIMode(boolean c) {}
        @Override public boolean isCIMode() { return false; }
        @Override public List<String> getTags() { return Collections.emptyList(); }
    }

    /** Minimal test-suite stub that holds a single test. */
    private static class StubSuite implements ITestSuite {

        private final StubTest test;
        private boolean failed = false;

        StubSuite(StubTest test) { this.test = test; }

        @Override public List<ITest>  getTests()  { return Collections.singletonList(test); }
        @Override public String       getName()    { return "stub-suite"; }
        @Override public String       getFilename(){ return "stub.nf.test"; }
        @Override public String       getDirectory(){ return ""; }
        @Override public void addProfile(String p) {}
        @Override public void setGlobalConfigFile(File f) {}
        @Override public void setFilename(String f) {}
        @Override public void configure(Config c) {}
        @Override public boolean hasSkippedTests()  { return false; }
        @Override public void    setFailedTests(boolean b) { failed = b; }
        @Override public boolean hasFailedTests()   { return failed; }
        @Override public SnapshotFile getSnapshot() { return null; }
        @Override public boolean hasSnapshotLoaded(){ return false; }
        @Override public void evalualteTestClosures() {}
        /** Mimics AbstractTestSuite.setupTest — delegates to test.setup(). */
        @Override public void setupTest(ITest t) throws Throwable { t.setup(null); }
        @Override public List<String> getTags() { return Collections.emptyList(); }
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private TestExecutionEngine engineWithRetries(int retries, StubTest test) {
        TestExecutionEngine engine = new TestExecutionEngine();
        engine.setListener(new NoopListener());
        engine.setTestSuites(Collections.singletonList(new StubSuite(test)));
        engine.setRetries(retries);
        return engine;
    }

    // ── tests ──────────────────────────────────────────────────────────────

    @Test
    public void testRetrySucceedsAfterNetworkError() throws Throwable {
        StubTest test = new StubTest(2, true); // fails twice, passes on 3rd attempt
        int exitCode = engineWithRetries(3, test).execute();

        assertEquals(0, exitCode, "should pass once the retry succeeds");
        assertEquals(3, test.executeCount.get(), "execute() should be called 3 times");
        // initial setup + 2 retries = 3 setup calls
        assertEquals(3, test.setupCount.get(), "setup() should be called once per attempt");
    }

    @Test
    public void testRetryExhaustedReturnsFailure() throws Throwable {
        StubTest test = new StubTest(5, true); // always fails (5 > retries)
        int exitCode = engineWithRetries(2, test).execute();

        assertEquals(1, exitCode, "should fail when all retries are exhausted");
        // 1 initial + 2 retries = 3 attempts total
        assertEquals(3, test.executeCount.get(), "execute() should be called retries+1 times");
    }

    @Test
    public void testNoRetryOnNonNetworkError() throws Throwable {
        StubTest test = new StubTest(1, false); // fails with non-network error
        int exitCode = engineWithRetries(3, test).execute();

        assertEquals(1, exitCode, "should fail immediately on non-network errors");
        assertEquals(1, test.executeCount.get(), "execute() should not be retried for non-network failures");
    }

    @Test
    public void testNoRetryWhenRetriesIsZero() throws Throwable {
        StubTest test = new StubTest(1, true); // network error, but retries disabled
        int exitCode = engineWithRetries(0, test).execute();

        assertEquals(1, exitCode, "should fail with retries=0");
        assertEquals(1, test.executeCount.get(), "execute() should not be retried when retries=0");
    }

    @Test
    public void testSuccessOnFirstAttemptNeverRetries() throws Throwable {
        StubTest test = new StubTest(0, true); // always succeeds
        int exitCode = engineWithRetries(3, test).execute();

        assertEquals(0, exitCode);
        assertEquals(1, test.executeCount.get(), "execute() should only be called once on success");
        assertEquals(1, test.setupCount.get());
    }
}
