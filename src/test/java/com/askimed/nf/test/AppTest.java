package com.askimed.nf.test;

import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;

public class AppTest {

	static {

		// AnsiColors.disable();
		// AnsiText.disable();

	}

	@Test
	public void testScript() throws Exception {

		App app = new App();
		app.run(new String[] { "test", "test-data/test2.nf.test", "test-data/test1.nf.test" });

	}

}
