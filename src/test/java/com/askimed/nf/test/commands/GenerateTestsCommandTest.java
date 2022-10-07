package com.askimed.nf.test.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;
import com.askimed.nf.test.core.ITestSuite;
import com.askimed.nf.test.lang.TestSuiteBuilder;
import com.askimed.nf.test.lang.function.FunctionTestSuite;
import com.askimed.nf.test.lang.pipeline.PipelineTestSuite;
import com.askimed.nf.test.lang.process.ProcessTestSuite;
import com.askimed.nf.test.lang.workflow.WorkflowTestSuite;
import com.askimed.nf.test.util.FileUtil;

public class GenerateTestsCommandTest {

	static {

		// AnsiColors.disable();
		// AnsiText.disable();

	}

	@BeforeEach
	public void setUp() throws IOException {

		FileUtil.deleteDirectory(new File(".nf-test"));
		new File("nf-test.config").delete();

		App app = new App();
		app.run(new String[] { "init" });
	}

	@Test
	public void testGeneratePipelineDsl1Test() throws Exception {

		FileUtil.deleteDirectory(new File("tests"));
		App app = new App();
		int exitCode = app.run(new String[] { "generate", "pipeline", "test-data/pipeline/dsl1/test1.nf" });
		assertEquals(0, exitCode);

		File testFile = new File("tests/test-data/pipeline/dsl1/test1.nf.test");
		assertTrue(testFile.exists());
		ITestSuite testSuite = TestSuiteBuilder.parse(testFile);
		assertEquals(1, testSuite.getTests().size());
		assertTrue(testSuite instanceof PipelineTestSuite);
	}

	@Test
	public void testGeneratePipelineDsl2Test() throws Exception {

		FileUtil.deleteDirectory(new File("tests"));
		App app = new App();
		int exitCode = app.run(new String[] { "generate", "pipeline", "test-data/pipeline/dsl2/trial.nf" });
		assertEquals(0, exitCode);

		File testFile = new File("tests/test-data/pipeline/dsl2/trial.nf.test");
		assertTrue(testFile.exists());
		ITestSuite testSuite = TestSuiteBuilder.parse(testFile);
		assertEquals(1, testSuite.getTests().size());
		assertTrue(testSuite instanceof PipelineTestSuite);
	}

	@Test
	public void testGenerateProcessTest() throws Exception {

		FileUtil.deleteDirectory(new File("tests"));
		App app = new App();
		int exitCode = app.run(new String[] { "generate", "process", "test-data/process/default/test_process.nf" });
		assertEquals(0, exitCode);

		File testFile = new File("tests/test-data/process/default/test_process.nf.test");
		assertTrue(testFile.exists());
		ITestSuite testSuite = TestSuiteBuilder.parse(testFile);
		assertEquals(1, testSuite.getTests().size());
		assertTrue(testSuite instanceof ProcessTestSuite);

	}
	
	@Test
	public void testGenerateProcessMultiTest() throws Exception {

		FileUtil.deleteDirectory(new File("tests"));
		App app = new App();
		int exitCode = app.run(new String[] { "generate", "process", "test-data/process/multi/processes.nf" });
		assertEquals(0, exitCode);

		assertTrue(new File("tests/test-data/process/multi/processes.one.nf.test").exists());

		File testFile = new File("tests/test-data/process/multi/processes.two.nf.test");
		assertTrue(testFile.exists());
		ITestSuite testSuite = TestSuiteBuilder.parse(testFile);
		assertEquals(1, testSuite.getTests().size());
		assertTrue(testSuite instanceof ProcessTestSuite);
	}
	
	@Test
	public void testGenerateFunctionTest() throws Exception {

		FileUtil.deleteDirectory(new File("tests"));
		App app = new App();
		int exitCode = app.run(new String[] { "generate", "function", "test-data/function/multi/functions.nf" });
		assertEquals(0, exitCode);

		File testFile = new File("tests/test-data/function/multi/functions.nf.test");
		assertTrue(testFile.exists());
		ITestSuite testSuite = TestSuiteBuilder.parse(testFile);
		assertEquals(2, testSuite.getTests().size());
		assertTrue(testSuite instanceof FunctionTestSuite);

	}
	
	@Test
	public void testGenerateWorkflowTest() throws Exception {

		FileUtil.deleteDirectory(new File("tests"));
		App app = new App();
		int exitCode = app.run(new String[] { "generate", "workflow", "test-data/pipeline/dsl2/trial.nf" });
		assertEquals(0, exitCode);

		File testFile = new File("tests/test-data/pipeline/dsl2/trial.nf.test");
		assertTrue(testFile.exists());
		ITestSuite testSuite = TestSuiteBuilder.parse(testFile);
		assertEquals(1, testSuite.getTests().size());
		assertTrue(testSuite instanceof WorkflowTestSuite);
	}

	@Test
	public void testSyntaxErrorInConfig() throws Exception {

		Files.copy(new File("test-data/nf-test-error.config").toPath(),
				new FileOutputStream(new File("nf-test.config")));

		App app = new App();
		int exitCode = app.run(new String[] { "generate", "workflow", "test-data/pipeline/dsl2/trial.nf" });
		assertEquals(2, exitCode);
		assertFalse(new File("tests/test-data/pipeline/dsl2/trial.nf.test").exists());

	}

}
