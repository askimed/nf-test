package com.askimed.nf.test.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;
import com.askimed.nf.test.util.FileUtil;

public class FunctionTest {

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
	public void testScriptSucces() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/function/default/functions.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testScriptWihtMultipleFunctions() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/function/multi/functions.nf.test" });
		assertEquals(0, exitCode);

	}
	
	@Test
	public void testGroovyScript() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/function/utils/Utils.groovy.test" ,"--debug"});
		assertEquals(0, exitCode);

	}


}
