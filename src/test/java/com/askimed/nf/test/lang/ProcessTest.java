package com.askimed.nf.test.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;
import com.askimed.nf.test.util.AnsiColors;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.FileUtil;

public class ProcessTest {

	static {

		 AnsiColors.disable();
		 AnsiText.disable();

	}

	@BeforeEach
	public void setUp() throws IOException {
		FileUtil.deleteDirectory(new File(".nf-test"));
		new File("nf-test.config").delete();
	}

	@Test
	public void testScriptSucces() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/default/test_process_success.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testDisableAutoSortConfig() throws Exception {

		Files.copy(Paths.get("test-data", "autosort.nf.test"), Paths.get("nf-test.config"));

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/autosort/test_process.nf.test" });
		// Fails, because expects sorted channels
		assertEquals(1, exitCode);

	}
	
	@Test
	public void testDisableAutoSortTestSuite() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/autosort/test_process_autosort.nf.test" });
		// Fails, because expects sorted channels
		assertEquals(1, exitCode);

	}
	
	@Test
	public void testDisableAutoSortTestSuiteAndOverwrite() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/autosort/test_process_autosort2.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testWithNoOutputs() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/no-outputs/process.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testWithNoOutputsAndAccess() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/no-outputs/process.failed.nf.test" });
		assertEquals(1, exitCode);

	}
	
	@Test
	public void testMissingScript() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/default/wrong-filename.nf.test" });
		assertEquals(1, exitCode);

	}

	@Test
	public void testScriptFailed() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/default/test_process_failed.nf.test" });
		assertEquals(1, exitCode);

	}

	@Test
	public void testScriptWithSyntaxError() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/default/test_process_syntax_error.nf.test" });
		assertEquals(1, exitCode);

	}

	@Test
	public void testScriptWithGlobalVariables() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/global-variables/process.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testPathUtilExtension() throws Exception {

		App app = new App();
		int exitCode = app
				.run(new String[] { "test", "test-data/process/path-util/test_process.nf.test", "--update-snapshot" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testBinFolder() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/bin-folder/test_process_success.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testConfig() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/config/test_process.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testChannelFolder() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/channel-folder/test_process.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testNestedParams() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/nested-params/process.nf.test" });
		assertEquals(0, exitCode);

	}
	
	@Test
	public void testLoadGzip() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/gzip/copy_gz.nf.test" });
		assertEquals(0, exitCode);

	}
}
