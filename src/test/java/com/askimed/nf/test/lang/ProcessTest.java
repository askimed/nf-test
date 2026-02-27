package com.askimed.nf.test.lang;

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

import static org.junit.jupiter.api.Assertions.*;

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
	public void testExample() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/example/test1.nf.test" });
		assertEquals(0, exitCode);

	}

	/*
	 * @Test public void testDisableAutoSortConfig() throws Exception {
	 * 
	 * Files.copy(Paths.get("test-data", "autosort.nf.test"),
	 * Paths.get("nf-test.config"));
	 * 
	 * App app = new App(); int exitCode = app.run(new String[] { "test",
	 * "test-data/process/autosort/test_process.nf.test" }); // Fails, because
	 * expects sorted channels assertEquals(1, exitCode);
	 * 
	 * }
	 */

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
	public void testNullValuesInChannels() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/channels/null-values/return_null.nf.test" });
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
		assertEquals(0, exitCode);

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

	@Test
	public void testLoadGzipWithModuleDir() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/gzip/tests/copy_gz.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testScriptWithRelativePath() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/default/test_process_relative.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testScriptWithRelativePathInSubfolder() throws Exception {

		App app = new App();
		int exitCode = app
				.run(new String[] { "test", "test-data/process/default/tests/test_process_relative.nf.test" });
		assertEquals(0, exitCode);

	}
	
	@Test
	public void testDependencies() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/dependencies/process_data.nf.test", "--verbose" });
  
  	}

	@Test
	public void testDependenciesWithAlias() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/dependencies/process_data_alias.nf.test", "--verbose" });

	}
  
  @Test
	public void testDependenciesAbricate() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/abricate/summary/tests/main.nf.test" });
		assertEquals(0, exitCode);

	}
  
	@Test
	public void testProfiles() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/profiles/hello.a.nf.test" });
		assertEquals(0, exitCode);

		app = new App();
		exitCode = app
				.run(new String[] { "test", "test-data/process/profiles/hello.a.nf.test", "--profile", "profile_b" });
		assertEquals(1, exitCode);

	}

	@Test
	public void testProfilesOverwrite() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/profiles/hello.b.nf.test" });
		assertEquals(1, exitCode);

		app = new App();
		exitCode = app
				.run(new String[] { "test", "test-data/process/profiles/hello.b.nf.test", "--profile", "profile_b" });
		assertEquals(0, exitCode);

	}
	
	@Test
	public void testProfilesOverwriteInConfig() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/profiles/hello.a.nf.test", "--config",
				"test-data/process/profiles/nf-test.config" });
		assertEquals(0, exitCode);

		app = new App();
		exitCode = app.run(new String[] { "test", "test-data/process/profiles/hello.c.nf.test", "--config",
				"test-data/process/profiles/nf-test.config", "--profile", "profile_b" });
		assertEquals(1, exitCode);

		app = new App();
		exitCode = app.run(new String[] { "test", "test-data/process/profiles/hello.b.nf.test", "--config",
				"test-data/process/profiles/nf-test.config", "--profile", "profile_b" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testRequires() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/profiles/hello.a.nf.test", "--config",
				"test-data/process/requires/nf-test-0.1.0.config" });
		assertEquals(0, exitCode);

		app = new App();
		exitCode = app.run(new String[] { "test", "test-data/process/profiles/hello.a.nf.test", "--config",
				"test-data/process/requires/nf-test-100.0.0.config" });
		assertEquals(2, exitCode);


	}


	@Test
	public void testUniquenessSnapshots() throws Exception {
		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/snapshots/unique.nf.test" });
		assertEquals(0, exitCode);
	}

	@Test
	public void testNotUniquenessOfSnapshots() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/snapshots/no-unique.nf.test" });
		assertEquals(1, exitCode);

	}

	@Test
	public void testMd5Snapshots() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/snapshots/md5.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testMd5ValidGzip() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/snapshots/gzip-success.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testMd5InvalidGzip() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/snapshots/gzip-fail.nf.test" });
		assertEquals(1, exitCode);
  }

	@Test
	public void testSnapshotsInCiMode() throws Exception {

		File snapshot = new File("test-data/process/snapshots/ci-mode.nf.test.snap");

		snapshot.delete();

		assertFalse(snapshot.exists());

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/process/snapshots/ci-mode.nf.test" });
		assertEquals(0, exitCode);

		assertTrue(snapshot.exists());

		snapshot.delete();

		assertFalse(snapshot.exists());

		app = new App();
		exitCode = app.run(new String[] { "test", "test-data/process/snapshots/ci-mode.nf.test", "--ci" });
		assertEquals(1, exitCode);

		assertFalse(snapshot.exists());

	}

}
