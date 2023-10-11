package com.askimed.nf.test.core;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.config.FileStaging;
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

	public boolean skipped = false;

	public static FileStaging[] SHARED_DIRECTORIES = { new FileStaging("bin"), new FileStaging("lib"),
			new FileStaging("assets") };

	protected File config = null;

	private boolean updateSnapshot = false;

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

	@Override
	public void setup(Config config, File testDirectory) throws IOException {

		if (testDirectory == null) {
			throw new IOException("Testcase setup failed: No home directory set");
		}

		launchDir = initDirectory("Launch Directory", testDirectory, DIRECTORY_TESTS, getHash());
		metaDir = initDirectory("Meta Directory", launchDir, DIRECTORY_META);
		outputDir = initDirectory("Output Directory", launchDir, DIRECTORY_OUTPUT);
		workDir = initDirectory("Working Directory", launchDir, DIRECTORY_WORK);

		try {
			// copy bin, assets and lib to metaDir
			shareDirectories(SHARED_DIRECTORIES, metaDir);
			if (config != null) {
				// copy user defined staging directories
				log.debug("Stage {} user provided files...", config.getStageBuilder().getPaths().size());
				shareDirectories(config.getStageBuilder().getPaths(), metaDir);
			}
			shareDirectories(parent.getStageBuilder().getPaths(), metaDir);
		} catch (Exception e) {
			throw new IOException("Testcase setup failed: Directories could not be shared:\n" + e);
		}

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

	public File initDirectory(String name, File root, String... childs) throws IOException {

		String path = FileUtil.path(root.getAbsolutePath(), FileUtil.path(childs));

		File directory = new File(path).getAbsoluteFile();
		try {
			FileUtil.deleteDirectory(directory);
			FileUtil.createDirectory(directory);
			return directory;
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

	protected void shareDirectories(List<FileStaging> directories, File stageDir) throws IOException {
		for (FileStaging directory : directories) {
			String metaDirectory = FileUtil.path(stageDir.getAbsolutePath(), directory.getPath());
			directory.stage(metaDirectory);
		}
	}

	protected void shareDirectories(FileStaging[] directories, File stageDir) throws IOException {
		for (FileStaging directory : directories) {
			String metaDirectory = FileUtil.path(stageDir.getAbsolutePath(), directory.getPath());
			directory.stage(metaDirectory);
		}
	}

	@Override
	public void setUpdateSnapshot(boolean updateSnapshot) {
		this.updateSnapshot = updateSnapshot;
	}

	public boolean isUpdateSnapshot() {
		return updateSnapshot;
	}

	@Override
	public String toString() {
		return getHash().substring(0, 8) + ": " + getName();
	}

}
