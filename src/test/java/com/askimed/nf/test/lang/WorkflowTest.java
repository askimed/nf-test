package com.askimed.nf.test.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;

public class WorkflowTest {

	static {

		// AnsiColors.disable();
		// AnsiText.disable();

	}

	@Test
	public void testWorkflowSucces() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/workflow/trial.nf.test", "--debug" });
		assertEquals(0, exitCode);

	}
}
