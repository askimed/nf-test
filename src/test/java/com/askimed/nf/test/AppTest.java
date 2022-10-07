package com.askimed.nf.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.util.FileUtil;

public class AppTest {

	static {

		// AnsiColors.disable();
		// AnsiText.disable();

	}

	@BeforeEach
	public void setUp() throws IOException {
		FileUtil.deleteDirectory(new File(".nf-test"));
		new File("nf-test.config").delete();
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

	@Test
	public void testSyntaxErrorInConfig1() throws Exception {

		Files.copy(new File("test-data/nf-test-error.config").toPath(),
				new FileOutputStream(new File("nf-test.config")));

		App app = new App();
		int exitCode = app.run(new String[] { "test", "example/say-hello.nf.test" });
		assertEquals(2, exitCode);

	}

	
	@Test
	public void testList() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "list", "example/trial1.test","example/trial2.test","example/trial3.test" });
		assertEquals(0, exitCode);

	}
	
	@Test
	public void testRunWithHash() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "example/trial1.test@d7dd8ca8" });
		assertEquals(0, exitCode);

	}
	
	@Test
	public void testSyntaxErrorInConfig2() throws Exception {

		Files.copy(new File("test-data/nf-test-error.config").toPath(),
				new FileOutputStream(new File("nf-test.config")));

		App app = new App();
		int exitCode = app.run(new String[] { "list", "example/say-hello.nf.test" });
		assertEquals(2, exitCode);

	}

}
