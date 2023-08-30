package com.askimed.nf.test.core;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.util.FileUtil;

public abstract class AbstractTest implements ITest {

	public File launchDir;

	public File metaDir;

	public File outputDir;

	public File workDir;

	public String baseDir = System.getProperty("user.dir");

	public String projectDir = System.getProperty("user.dir");

	public boolean skipped = false;

	public static String[] SHARED_DIRECTORIES = { "bin", "lib", "assets" };

	protected File config = null;

	private boolean updateSnapshot = false;

	private boolean debug = false;

	private boolean withTrace = true;

	private List<String> tags = new Vector<String>();

	private AbstractTestSuite parent;

	private String options;

	public AbstractTest(AbstractTestSuite parent) {
		this.parent = parent;
		options = parent.getOptions();
	}

	public void config(String config) {
		this.config = new File(config);
	}

	public File getConfig() {
		return config;
	}

	@Override
	public void setup(String testDirectory) throws IOException {

		launchDir = new File(FileUtil.path(testDirectory, "tests", getHash()));
		metaDir = new File(FileUtil.path(launchDir.getAbsolutePath(), "meta"));
		outputDir = new File(FileUtil.path(launchDir.getAbsolutePath(), "output"));
		workDir = new File(FileUtil.path(launchDir.getAbsolutePath(), "work"));

		initDirectory("Launch Directory", launchDir);
		initDirectory("Meta Directory", metaDir);
		initDirectory("Output Directory", outputDir);
		initDirectory("Working Directory", workDir);

		try {
			// copy bin and lib to metaDir. TODO: use symlinks and read additional "mapping"
			// from config file
			shareDirectories(SHARED_DIRECTORIES, metaDir);
		} catch (Exception e) {
			throw new IOException("Directories could not be shared:\n" + e);
		}

	}

	public void initDirectory(String name, File directory) throws IOException {
		try {
			FileUtil.deleteDirectory(directory);
			FileUtil.createDirectory(directory);
		} catch (Exception e) {
			throw new IOException(name + " '" + directory + "' could not be deleted or created:\n" + e);
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

		return hash(parent.getFilename() + getName());

	}

	private String hash(String value) {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(value.getBytes());
			byte[] md5sum = md.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			return bigInt.toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "??";
		}
	}

	public void tag(String tag) {
		tags.add(tag);
	}

	@Override
	public List<String> getTags() {
		return tags;
	}

	public void options(String options) {
		this.options = options;
	}

	public String getOptions() {
		return options;
	}

	@Override
	public AbstractTestSuite getParent() {
		return parent;
	}

	@Override
	public void skip() {
		skipped = true;
	}

	public boolean isSkipped() {
		return skipped;
	}

	@Override
	public ITestSuite getTestSuite() {
		return parent;
	}

	public void debug(boolean debug) {
		setDebug(debug);
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isDebug() {
		return debug;
	}

	@Override
	public void setWithTrace(boolean withTrace) {
		this.withTrace = withTrace;
	}

	public boolean isWithTrace() {
		return withTrace;
	}

	protected void shareDirectories(String[] directories, File metaDir) throws IOException {
		for (String directory : directories) {
			File localDirectory = new File(directory);
			if (localDirectory.exists()) {
				String metaDirectory = FileUtil.path(metaDir.getAbsolutePath(), directory);
				FileUtil.copyDirectory(localDirectory.getAbsolutePath(), metaDirectory);
			}
		}
	}

	@Override
	public void setUpdateSnapshot(boolean updateSnapshot) {
		this.updateSnapshot = updateSnapshot;
	}

	public boolean isUpdateSnapshot() {
		return updateSnapshot;
	}

}
