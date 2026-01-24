package com.askimed.nf.test.core;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.util.FileUtil;

public abstract class AbstractTest implements ITest {

	public static final String FILE_STD_ERR = "std.err";

	public static final String FILE_STD_OUT = "std.out";

	public static final String FILE_TRACE = "trace.csv";

	public static final String FILE_NEXTFLOW_LOG = "nextflow.log";

	public static final String FILE_MOCK = "mock.nf";

	public static final String FILE_PARAMS = "params.json";

	public static final String FILE_WORKFLOW_JSON = "workflow.json";

	public static final String FILE_FUNCTION_JSON = "function.json";

	public static final String DIRECTORY_WORK = "work";

	public static final String DIRECTORY_OUTPUT = "output";

	public static final String DIRECTORY_META = "meta";

	public static final String DIRECTORY_TESTS = "tests";

	public File launchDir;

	public File metaDir;

	public File outputDir;

	public File workDir;

	public File moduleDir;

	public File moduleTestDir;

	public File baseDir = new File(System.getProperty("user.dir"));

	public File projectDir = new File(System.getProperty("user.dir"));

	public File mockFile;

	public boolean skipped = false;

	protected File config = null;

	private boolean updateSnapshot = false;

	private boolean ciMode = false;
	private Map<String, Object> parameters = new HashMap<String, Object>();

	public AbstractTest() {

	private boolean debug = false;

	private boolean withTrace = true;

	private List<String> tags = new Vector<String>();

	private AbstractTestSuite parent;

	private String options;

	private static Logger log = LoggerFactory.getLogger(AbstractTest.class);

	public AbstractTest(AbstractTestSuite parent) {
		this.parent = parent;
		options = parent.getOptions();
	}

	public void config(String config) {
		if (config == null) {
			return;
		}
		if (parent.isRelative(config)) {
			this.config = new File(parent.makeAbsolute(config));
		} else {
			this.config = new File(config);
		}
	}

	public File getConfig() {
		return config;
	}

	public void defineDirectories(File testDirectory) throws IOException {

		if (testDirectory == null) {
			throw new IOException("Testcase setup failed: No home directory set");
		}

		launchDir = constructDirectory(testDirectory, DIRECTORY_TESTS, getHash());
		metaDir = constructDirectory(launchDir, DIRECTORY_META);
		outputDir = constructDirectory(launchDir, DIRECTORY_OUTPUT);
		workDir = constructDirectory(launchDir, DIRECTORY_WORK);

		mockFile = new File( ".nf-test-" + getHash() + ".nf");
		mockFile.deleteOnExit();
	}

	@Override
	public void setup(Config config) throws IOException {
		setupDirectory("Launch Directory", launchDir);
		setupDirectory("Meta Directory", metaDir);
		setupDirectory("Output Directory", outputDir);
		setupDirectory("Working Directory", workDir);
	}

	@Override
	public void execute() throws Throwable {
		if (parent.getScript() != null) {
			moduleDir = new File(parent.getScript()).getAbsoluteFile().getParentFile();
		}
		if (parent.getFilename() != null) {
			moduleTestDir = new File(parent.getDirectory()).getAbsoluteFile();
		}
	}

	private File constructDirectory(File root, String... childs) {
		String path = FileUtil.path(root.getAbsolutePath(), FileUtil.path(childs));
		File directory = new File(path).getAbsoluteFile();
		return directory;
	}

	private void setupDirectory(String name, File directory) throws IOException {

		try {
			FileUtil.deleteDirectory(directory);
			FileUtil.createDirectory(directory);
		} catch (Exception e) {
			throw new IOException(name + " '" + directory + "' could not be deleted or created:\n" + e);
		}

	}

	@Override
	public void cleanup() {

	}

	@Override
	public String getErrorReport() throws Throwable {
		String result = null;
		File outFile = new File(metaDir, FILE_STD_OUT);
		if (outFile.exists()) {
			result = "Nextflow stdout:\n\n" + FileUtil.readFileAsString(outFile);
		}
		File errFile = new File(metaDir, FILE_STD_ERR);
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

		if (parent == null || parent.getFilename() == null || getName() == null || getName().isEmpty()) {
			throw new RuntimeException("Error generating hash");
		}

		return HashUtil.getMd5(parent.getFilename() + getName());

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

	@Override
	public void setUpdateSnapshot(boolean updateSnapshot) {
		this.updateSnapshot = updateSnapshot;
	}

	public boolean isUpdateSnapshot() {
		return updateSnapshot;
	}

	@Override
	public void setCIMode(boolean ciMode) {
		this.ciMode = ciMode;
	}

	@Override
	public boolean isCIMode() {
		return ciMode;
	}

	@Override
	public String toString() {
		return getHash().substring(0, 8) + ": " + getName();
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}
	
}
