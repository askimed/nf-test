package com.askimed.nf.test.core;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.util.FileUtil;

public abstract class AbstractTest implements ITest {

	public File metaDir;

	public File outputDir;

	public File workDir;

	public String baseDir = System.getProperty("user.dir");

	public boolean skipped = false;

	public AbstractTest() {

	}

	protected String getWorkDir() {

		File workDir = new File("nf-test");

		try {

			Config config = Config.parse(new File(Config.FILENAME));
			workDir = new File(config.getWorkDir());
		} catch (Exception e) {

		}
		return workDir.getAbsolutePath();
	}

	@Override
	public void setup(File baseDir) throws IOException {
		String metaDir = FileUtil.path(baseDir.getAbsolutePath(), "tests", getHash(), "meta");

		try {
			this.metaDir = new File(metaDir);
			FileUtil.deleteDirectory(this.metaDir);
			FileUtil.createDirectory(this.metaDir);

			// copy bin and lib to metaDir. TODO: use symlinks and read additional "mapping"
			// from config file
			File lib = new File("lib");
			if (lib.exists()) {
				String metaDirLib = FileUtil.path(metaDir, "lib");
				FileUtil.copyDirectory(lib.getAbsolutePath(), metaDirLib);
			}

			File bin = new File("bin");
			if (bin.exists()) {
				String metaDirBin = FileUtil.path(metaDir, "lib");
				FileUtil.copyDirectory(bin.getAbsolutePath(), metaDirBin);
			}

		} catch (Exception e) {
			throw new IOException("Meta Directory '" + metaDir + "' could not be deleted:\n" + e);
		}

		String outputDir = FileUtil.path(baseDir.getAbsolutePath(), "tests", getHash(), "output");

		try {
			this.outputDir = new File(outputDir);
			FileUtil.deleteDirectory(this.outputDir);
			FileUtil.createDirectory(this.outputDir);
		} catch (Exception e) {
			throw new IOException("Output Directory '" + outputDir + "' could not be deleted:\n" + e);
		}

		String workDir = FileUtil.path(baseDir.getAbsolutePath(), "tests", getHash(), "work");
		try {
			this.workDir = new File(workDir);
			FileUtil.deleteDirectory(this.workDir);
			FileUtil.createDirectory(this.workDir);
		} catch (Exception e) {
			throw new IOException("Working Directory '" + workDir + "' could not be deleted:\n" + e);
		}
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

	@Override
	public void skip() {
		skipped = true;
	}

	public boolean isSkipped() {
		return skipped;
	}

}
