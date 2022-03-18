package com.askimed.nf.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class AppTest {

	static {

		// AnsiColors.disable();
		// AnsiText.disable();

	}

	@Test
	public void testMultipleScripts() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/test2.nf.test", "test-data/test1.nf.test" });
		assertEquals(1, exitCode);
	}

	@Test
	public void testScript() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/test1.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testPathUtil() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/path-util/test_process.nf.test", "--debug" });
		assertEquals(0, exitCode);

	}
	
	@Test
	public void testHelloWorld() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "example/hello.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testSayHello() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "example/say-hello.nf.test" });
		assertEquals(0, exitCode);

	}
	
}
