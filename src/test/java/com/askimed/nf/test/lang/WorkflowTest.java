package com.askimed.nf.test.lang;

import static org.junit.jupiter.api.Assertions.*;

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
	public void testWorkflowWithRelativePath() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/default/tests/trial.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testWorkflowUnamedOutputs() throws Exception {
		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/unnamed/trial.unnamed.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testWorkflowAndSnapshot() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/default/trial.snapshot.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testWorkflowWithNoOutputs() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/no-outputs/trial.nf.test" });
		assertEquals(1, exitCode);

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
		int exitCode = app
				.run(new String[] { "test", "test-data/workflow/libs/hello.nf.test", "--lib", "lib", "--debug" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testParamsIssue34() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/issue34/trial.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testParamsIssue34Setup() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/issue34/trial.setup.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testHangingWorkflowIssue57() throws Exception {

		App app = new App();
		int exitCode = app
				.run(new String[] { "test", "test-data/workflow/hanging/meaningless_workflow.nf.test", "--debug" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testWorkflowNonUniqueFilenames() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/non-unique-filenames/main.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testIssue125() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/issue125/example_wf.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testStagingWithoutMapping() throws Exception {

		App app = new App();
		// TODO: remove this test. no staging needed.
		//int exitCode = app.run(new String[] { "test", "test-data/workflow/staging/hello.nf.test" });
		//assertEquals(1, exitCode);

	}

	@Test
	public void testStagingWitMappingFolder() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/staging/hello.nf.test", "--config",
				"test-data/workflow/staging/nf-test.folder.config", "--debug" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testStagingWitMappingFile() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/staging/hello.nf.test", "--config",
				"test-data/workflow/staging/nf-test.file.config" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testStagingWitMappingFileAndMode() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/staging/hello.nf.test", "--config",
				"test-data/workflow/staging/nf-test.file.mode.config" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testStagingInTestsuite() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/staging/hello-stage.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testRegex() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/regex/workflow.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testWorkflowKeepsLaunchDirByDefault() throws Exception {

		App app = new App();

		int exitCode = app.run(new String[] {
			"test",
			"test-data/workflow/regex/workflow.nf.test"
		});

		assertEquals(0, exitCode);

		File testsRoot = new File(".nf-test/tests");
		File[] testDirs = testsRoot.listFiles(File::isDirectory);
		assertNotNull(testDirs, "Could not list test directories");

		assertTrue(
			testDirs.length == 1,
			"Expected test directories to exist without --no-save"
		);

		for (File testDir : testDirs) {
			File workDir = new File(testDir, "work");

			assertTrue(
				workDir.exists(),
				"Launch and Work directory should exist without --no-save"
			);
		}
	}

	@Test
	public void testWorkflowNoSaveDeletesLaunchDir() throws Exception {

		App app = new App();

		String testPath = "test-data/workflow/regex/workflow.nf.test";

		// Run test with --no-save
		int exitCode = app.run(new String[] {
			"test",
			testPath,
			"--no-save"
		});

		assertEquals(0, exitCode);

		// Locate .nf-test directory
		File nfTestDir = new File(".nf-test/tests");
		File[] remaining = nfTestDir.listFiles(file -> file.isDirectory());

		assertNotNull(remaining, "Could not list test directories");

		assertEquals(
			0,
			remaining.length,
			"Expected no test directories to remain when --no-save is enabled"
		);
	}

	@Test
	public void testWorkflowTopics() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/topics/main.nf.test" });
		assertEquals(0, exitCode);

	}

}
