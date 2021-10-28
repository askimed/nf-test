package com.askimed.nf.test.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;

public class GenerateTestsCommandTest {

	static {

		// AnsiColors.disable();
		// AnsiText.disable();

	}

	@BeforeAll
	public static void setUp() {
		App app = new App();
		app.run(new String[] { "init" });
	}

	@Test
	public void testGenerateWorkflowTest() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "generate", "pipeline", "test-data/test1.nf" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testGenerateProcessTest() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "generate", "process", "test-data/test_process.nf" });
		assertEquals(0, exitCode);

	}

}
