package com.askimed.nf.test.plugins;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;
import com.askimed.nf.test.util.FileUtil;

public class PluginsTest {

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
	public void testScriptInConfig() throws Exception {

		Files.copy(Paths.get("test-data", "plugins.nf.test"), Paths.get("nf-test.config"));

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/extensions/fasta/copy_fasta.nf.test" });
		assertEquals(0, exitCode);

	}

}
