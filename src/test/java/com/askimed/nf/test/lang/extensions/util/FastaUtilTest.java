package com.askimed.nf.test.lang.extensions.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;
import com.askimed.nf.test.util.FileUtil;

public class FastaUtilTest {

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
	public void testScript() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/extensions/fasta/copy_fasta.nf.test" });
		assertEquals(0, exitCode);

	}

}
