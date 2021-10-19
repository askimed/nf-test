package com.github.lukfor.testflight.lang;

import org.junit.jupiter.api.Test;

import com.github.lukfor.testflight.App;

public class ProcessTestTest {

	static {

		// AnsiColors.disable();
		// AnsiText.disable();

	}

	@Test
	public void testScript() throws Exception {

		App app = new App();
		app.run(new String[] { "test-data/test_process.nf.test" });

	}

}
