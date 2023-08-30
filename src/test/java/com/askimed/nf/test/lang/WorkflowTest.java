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
		int exitCode = app.run(new String[] { "test", "test-data/workflow/libs/hello.nf.test", "--lib", "lib" ,"--debug"});
		assertEquals(0, exitCode);

	}
	
	@Test
	public void testParamsIssue34() throws Exception {

	  App app = new App();
	  int exitCode = app.run(new String[] { "test", "test-data/workflow/issue34/trial.nf.test"});
	  assertEquals(0, exitCode);

	}
	
	@Test
	public void testParamsIssue34Setup() throws Exception {

	  App app = new App();
	  int exitCode = app.run(new String[] { "test", "test-data/workflow/issue34/trial.setup.nf.test"});
	  assertEquals(0, exitCode);

	}
	
	@Test
	public void testHangingWorkflowIssue57() throws Exception {

	  App app = new App();
	  int exitCode = app.run(new String[] { "test", "test-data/workflow/hanging/meaningless_workflow.nf.test","--debug"});
	  assertEquals(0, exitCode);

	}

}
