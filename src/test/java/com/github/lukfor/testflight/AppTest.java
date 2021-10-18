package com.github.lukfor.testflight;

import org.junit.jupiter.api.Test;

public class AppTest {

	static {

		// AnsiColors.disable();
		// AnsiText.disable();

	}

	@Test
	public void testScript() throws Exception {

		App app = new App();
		app.run(new String[] { "test-data/test2.nf.test", "test-data/test1.nf.test" });

	}

}
