package com.askimed.nf.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.util.FileUtil;

public class AppTest {

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
