package com.askimed.nf.test.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;
import com.askimed.nf.test.util.FileUtil;

public class PipelineTest {

	@BeforeAll
	public static void setUp() throws IOException {
		FileUtil.deleteDirectory(new File(".nf-test"));
		new File("nf-test.config").delete();
	}

	@Test
	public void testDsl1() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/pipeline/dsl1/test1.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testMultipleDsl1Scripts() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/pipeline/dsl1/test2.nf.test",
				"test-data/pipeline/dsl1/test1.nf.test" });
		assertEquals(1, exitCode);
	}

	@Test
	public void testDsl2() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/pipeline/dsl2/trial.nf.test" });
		assertEquals(0, exitCode);

	}
}
