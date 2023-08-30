package com.askimed.nf.test.core;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.util.FileUtil;

public abstract class AbstractTest implements ITest {

	public File launchDir;

	public File metaDir;

	public File outputDir;

	public File workDir;

	public String baseDir = System.getProperty("user.dir");

	public boolean skipped = false;

	private ITestSuite suite;

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

		String launchDir = FileUtil.path(baseDir.getAbsolutePath(), "tests", getHash());

		try {
			this.launchDir = new File(launchDir);
			FileUtil.deleteDirectory(this.launchDir);
			FileUtil.createDirectory(this.launchDir);
		} catch (Exception e) {
			throw new IOException("Launch Directory '" + launchDir + "' could not be deleted or created:\n" + e);
		}

		String metaDir = FileUtil.path(launchDir, "meta");
		this.metaDir = new File(metaDir);
		try {
			FileUtil.deleteDirectory(this.metaDir);
			FileUtil.createDirectory(this.metaDir);
		} catch (Exception e) {
			throw new IOException("Meta Directory '" + metaDir + "' could not be deleted:\n" + e);
		}

		try {
			// copy bin and lib to metaDir. TODO: use symlinks and read additional "mapping"
			// from config file
			shareDirectories(SHARED_DIRECTORIES, metaDir);
		} catch (Exception e) {
			throw new IOException("Directories could not be shared:\n" + e);
		}

		String outputDir = FileUtil.path(launchDir, "output");

		try {
			this.outputDir = new File(outputDir);
			FileUtil.deleteDirectory(this.outputDir);
			FileUtil.createDirectory(this.outputDir);
		} catch (Exception e) {
			throw new IOException("Output Directory '" + outputDir + "' could not be deleted:\n" + e);
		}

		String workDir = FileUtil.path(launchDir, "work");
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

		return hash(suite.getFilename() + getName());

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
	public void setTestSuite(ITestSuite suite) {
		this.suite = suite;
	}

	@Override
	public ITestSuite getTestSuite() {
		return suite;
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

	protected void shareDirectories(String[] directories, String metaDir) throws IOException {
		for (String directory : directories) {
			File localDirectory = new File(directory);
			if (localDirectory.exists()) {
				String metaDirectory = FileUtil.path(metaDir, directory);
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
