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

		assertTrue(new File("tests/test-data/pipeline/dsl1/test1.nf.test").exists());

	}

	@Test
	public void testGeneratePipelineDsl2Test() throws Exception {

		FileUtil.deleteDirectory(new File("tests"));
		App app = new App();
		int exitCode = app.run(new String[] { "generate", "pipeline", "test-data/pipeline/dsl2/trial.nf" });
		assertEquals(0, exitCode);

		assertTrue(new File("tests/test-data/pipeline/dsl2/trial.nf.test").exists());

	}

	@Test
	public void testGenerateProcessTest() throws Exception {

		FileUtil.deleteDirectory(new File("tests"));
		App app = new App();
		int exitCode = app.run(new String[] { "generate", "process", "test-data/process/default/test_process.nf" });
		assertEquals(0, exitCode);

		assertTrue(new File("tests/test-data/process/default/test_process.nf.test").exists());

	}

	@Test
	public void testGenerateWorkflowTest() throws Exception {

		FileUtil.deleteDirectory(new File("tests"));
		App app = new App();
		int exitCode = app.run(new String[] { "generate", "workflow", "test-data/pipeline/dsl2/trial.nf" });
		assertEquals(0, exitCode);

		assertTrue(new File("tests/test-data/pipeline/dsl2/trial.nf.test").exists());

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
