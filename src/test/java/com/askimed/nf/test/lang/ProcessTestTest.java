package com.askimed.nf.test.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;

public class ProcessTestTest {

	static {

		// AnsiColors.disable();
		// AnsiText.disable();

	}

	@Test
	public void testScriptSucces() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/test_process.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testMissingScript() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/wrong-filename.nf.test" });
		assertEquals(1, exitCode);

	}

	@Test
	public void testScriptFailed() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/failures/test_process_failed.nf.test" });
		assertEquals(1, exitCode);

	}

	@Test
	public void testScriptWithSyntaxError() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/failures/test_process_syntax_error.nf.test" });
		assertEquals(1, exitCode);

	}
	
	@Test
	public void testScriptWithVariables() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/variables/process.nf.test" });
		assertEquals(0, exitCode);

	}

}
