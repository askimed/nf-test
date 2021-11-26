package com.askimed.nf.test.core;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.askimed.nf.test.util.FileUtil;

public abstract class AbstractTest implements ITest {

	protected File metaDir;

	protected File outputDir;

	public AbstractTest() {

	}

	@Override
	public void setup(File baseDir) {
		String metaDir = FileUtil.path(baseDir.getAbsolutePath(), "tests", getHash(), "meta");
		this.metaDir = new File(metaDir);
		FileUtil.deleteDirectory(this.metaDir);
		FileUtil.createDirectory(this.metaDir);

		String outputDir = FileUtil.path(baseDir.getAbsolutePath(), "tests", getHash(), "output");
		this.outputDir = new File(outputDir);
		FileUtil.deleteDirectory(this.outputDir);
		FileUtil.createDirectory(this.outputDir);

	}

	@Override
	public void cleanup() {
		// FileUtil.deleteDirectory(metaDir);
	}

	@Override
	public String getErrorReport() throws Throwable {
		String result = null;
		File outFile = new File(metaDir, "std.out");
		if (outFile.exists()) {
			result = "Nextflow stdout:\n\n" + FileUtil.readFileAsString(outFile);
		}
		File errFile = new File(metaDir, "std.err");
		if (errFile.exists()) {
			if (result == null) {
				result = "";
			}
			result += "Nextflow stderr:\n\n" + FileUtil.readFileAsString(errFile);
		}
		return result;
	}

	@Override
	public String getHash() {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(getName().getBytes());
			byte[] md5sum = md.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			return bigInt.toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "??";
		}
	}

}
