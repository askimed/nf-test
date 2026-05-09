package com.askimed.nf.test.lang;

import com.askimed.nf.test.App;
import com.askimed.nf.test.util.FileUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnapshotTest {

	static {

		//AnsiColors.disable();
		//AnsiText.disable();

	}

	@BeforeAll
	public static void setUp() throws IOException {
		FileUtil.deleteDirectory(new File(".nf-test"));
		new File("nf-test.config").delete();
	}

	@Test
	public void testSnapshotTransformer() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/snapshots/snapshot_transformer.nf.test" });
		assertEquals(0, exitCode);

	}

}
