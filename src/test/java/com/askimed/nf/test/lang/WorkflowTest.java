package com.askimed.nf.test.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;
import com.askimed.nf.test.util.FileUtil;

public class WorkflowTest {

	static {

		// AnsiColors.disable();
		// AnsiText.disable();

	}

	@BeforeAll
	public static void setUp() throws IOException {
		FileUtil.deleteDirectory(new File(".nf-test"));
		new File("nf-test.config").delete();
	}

	@Test
	public void testWorkflowSucces() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/default/trial.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testWorkflowWithNoOutputs() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/no-outputs/trial.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testOverrideWorkflow() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/multi/trial.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testLibs() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/libs/hello.nf.test", "--lib", "lib" });
		assertEquals(0, exitCode);

	}

}
