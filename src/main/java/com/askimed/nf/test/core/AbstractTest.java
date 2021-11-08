package com.askimed.nf.test.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.askimed.nf.test.util.FileUtil;

public abstract class AbstractTest implements ITest {

	protected File directory = null;

	@Override
	public void setup() {
		try {
			directory = Files.createTempDirectory("nf-test").toFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup() {
		FileUtil.deleteDirectory(directory);
	}

	@Override
	public String getErrorReport() throws Throwable {
		File outFile = new File(directory, "std.out");
		if (outFile.exists()) {
			return FileUtil.readFileAsString(outFile);
		} else {
			return null;
		}
	}

}
