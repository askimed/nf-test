package com.askimed.nf.test.core.reports;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.core.ITaggable;
import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.core.ITestSuite;
import com.askimed.nf.test.core.TestExecutionEngine;
import com.askimed.nf.test.core.TestExecutionResult;
import com.askimed.nf.test.core.TestSuiteExecutionResult;
import com.askimed.nf.test.lang.TestSuiteBuilder;
import com.askimed.nf.test.lang.extensions.SnapshotFile;
import com.opencsv.CSVReader;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;


// DummyTestSuit
class DummyTestSuite implements ITestSuite {

    // List of ITest
    private List<ITest> tests = new ArrayList<>();
    private String name;
    private boolean failedTests = false;
    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFilename() {
        return null;
    }

    @Override
    public List<ITest> getTests() {
        return tests;
    }

    public void addToTests(ITest test) {
        tests.add(test);
    }

    @Override
    public List<String> getTags() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTags'");
    }

    @Override
    public ITaggable getParent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getParent'");
    }

    @Override
    public void addProfile(String profile) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addProfile'");
    }

    @Override
    public void setGlobalConfigFile(File config) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setGlobalConfigFile'");
    }

    @Override
    public void setFilename(String filename) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setFilename'");
    }

    @Override
    public String getDirectory() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDirectory'");
    }

    @Override
    public void configure(Config config) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'configure'");
    }

    @Override
    public boolean hasSkippedTests() {
        return false;
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'hasSkippedTests'");
    }

    @Override
    public void setFailedTests(boolean b) {
        failedTests = b;
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'setFailedTests'");
    }

    @Override
    public boolean hasFailedTests() {
        return failedTests;
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'hasFailedTests'");
    }

    @Override
    public SnapshotFile getSnapshot() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSnapshot'");
    }

    @Override
    public boolean hasSnapshotLoaded() {
        return false;
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'hasSnapshotLoaded'");
    }

    @Override
    public void evalualteTestClosures() throws Throwable {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evalualteTestClosures'");
    }

    @Override
    public void setupTest(ITest test) throws Throwable {
        // NOTHING
    }
}


// DummyTest
class DummyTest implements ITest {

    private String name;
    private ITestSuite testSuite;
    @Override
    public List<String> getTags() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTags'");
    }

    @Override
    public ITaggable getParent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getParent'");
    }

    @Override
    public void defineDirectories(File testDirectory) throws Throwable {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'defineDirectories'");
    }

    @Override
    public void setup(Config config) throws Throwable {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setup'");
    }

    @Override
    public void execute() throws Throwable {
        try {
            Thread.sleep((long) (Math.random() * 100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cleanup() throws Throwable {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'cleanup'");
    }

    @Override
    public String getErrorReport() throws Throwable {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getErrorReport'");
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void skip() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'skip'");
    }

    @Override
    public boolean isSkipped() {
        return false;
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'isSkipped'");
    }

    @Override
    public void setDebug(boolean debug) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'setDebug'");
    }

    @Override
    public String getHash() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHash'");
    }

    @Override
    public ITestSuite getTestSuite() {
        // TODO Auto-generated method stub
        return testSuite;
    }

    public void setTestSuite(ITestSuite testSuite) {
        this.testSuite = testSuite;
    }

    @Override
    public void setWithTrace(boolean withTrace) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'setWithTrace'");
    }

    @Override
    public void setUpdateSnapshot(boolean updateSnapshot) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'setUpdateSnapshot'");
    }

    @Override
    public boolean isUpdateSnapshot() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isUpdateSnapshot'");
    }

    @Override
    public void setCIMode(boolean ciMode) {
        // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'setCIMode'");
    }

    @Override
    public boolean isCIMode() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isCIMode'");
    }

    
}

public class CsvReportWriterTest {

    private File tempFile;
    private CsvReportWriter writer;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".csv").toFile();
        System.out.println("Temp file: " + tempFile.getAbsolutePath());
        writer = new CsvReportWriter(tempFile.getAbsolutePath());
    }

    @AfterEach
    public void tearDown() {
        tempFile.delete();
    }

    @Test
    public void testConcurrentExecution() throws Throwable {
        // Create TestExecutionEngine and CsvReportWriter
        TestExecutionEngine engine = new TestExecutionEngine();
        engine.setDebug(true);


        // Create a dummy list of TestSuites with Tests
        // 100 TestSuites, with each having 1-10 random Tests
        List<ITestSuite> testSuites = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            // From Interface ITestSuite, create a TestSuite with a random number of Tests
            DummyTestSuite testSuite = new DummyTestSuite();
            testSuite.setName("TestSuite " + i);
            // Add the TestSuite to the list of TestSuites
            testSuites.add(testSuite);
            // Create a random number of Tests
            int numTests = (int) (Math.random() * 10) + 1;
            
            for (int j = 0; j < numTests; j++) {
                // From Interface ITest, create a Test
                DummyTest test = new DummyTest();
                test.setName("Test " + j);
                // Add the Test to the TestSuite
                testSuite.addToTests(test);
                test.setTestSuite(testSuite);
            }
        }

        System.out.println("TEST CASES SETUP");

        // Run the TestExecutionEngine with the list of TestSuites and the CsvReportWriter
        engine.setListener(writer);
        engine.setTestSuites(testSuites);

        // Run the TestExecutionEngine
        engine.execute();
        
        // Print temp file
        // Get the TestSuite -> Test and Result from tempFile
        CSVReader reader = new CSVReader(Files.newBufferedReader(tempFile.toPath()));
        // Maps to store generated TestSuites, Tests, and Results
        Map<String, List<String>> testSuiteTestMap = new HashMap<>();
        Map<String, String> testResultMap = new HashMap<>();

        String[] line;

        while ((line = reader.readNext()) != null) {
            // Skip the header
            if (line[0].equals("filename")) {
                continue;
            }

            // Get the TestSuite, Test, and Result and add it to the map
            String testSuiteName = line[1];
            String testName = line[3];
            String result = line[4];

            if (!testSuiteTestMap.containsKey(testSuiteName)) {
                testSuiteTestMap.put(testSuiteName, new ArrayList<>());
            }
            testSuiteTestMap.get(testSuiteName).add(testName);
            testResultMap.put(testName, result);
        }

        // Assert that the list of TestSuiteExecutionResults is the same as the dummy list of TestSuites
        assertEquals(testSuites.size(), testSuiteTestMap.size());

        // Assert that each TestSuiteExecutionResult has the same number of Tests as the dummy list of TestSuites
        for (ITestSuite testSuite : testSuites) {
            assertEquals(testSuite.getTests().size(), testSuiteTestMap.get(testSuite.getName()).size());
        }

        // Assert that each TestExecutionResult in each TestSuiteExecutionResult has the same Test as the dummy list of TestSuites
        for (ITestSuite testSuite : testSuites) {
            for (ITest test : testSuite.getTests()) {
                assertEquals(testResultMap.get(test.getName()), "PASSED");
            }
        }
    }
}