package com.askimed.nf.test.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

import com.askimed.nf.test.core.*;
import com.askimed.nf.test.core.reports.CsvReportWriter;
import com.askimed.nf.test.lang.dependencies.Coverage;
import com.askimed.nf.test.lang.dependencies.DependencyExporter;
import com.askimed.nf.test.lang.dependencies.DependencyResolver;
import com.askimed.nf.test.lang.dependencies.IMetaFile;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.GitCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.core.reports.TapTestReportWriter;
import com.askimed.nf.test.core.reports.XmlReportWriter;
import com.askimed.nf.test.lang.TestSuiteBuilder;
import com.askimed.nf.test.nextflow.NextflowCommand;
import com.askimed.nf.test.plugins.PluginManager;
import com.askimed.nf.test.util.AnsiColors;

import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "test")
public class RunTestsCommand extends AbstractCommand {

	private static final String SHARD_STRATEGY_ROUND_ROBIN = "round-robin";

	@Parameters(description = "test dependencies")
	private List<File> testPaths = new ArrayList<File>();

	@Option(names = {
			"--debug" }, description = "Show debugging infos and dump channels", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean debug = false;

	@Option(names = {
			"--verbose" }, description = "Show Nextflow output", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean verbose = false;

	@Option(names = {
			"--profile" }, description = "Profile to execute all tests", required = false, showDefaultValue = Visibility.ALWAYS)
	private String profile = null;

	@Option(names = {
			"--without-trace" }, description = "Run nextflow tests without trace parameter.", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean withoutTrace = false;

	@Option(names = {
			"--tap" }, description = "Write test results to tap file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String tap = null;

	@Option(names = {
			"--junitxml" }, description = "Write test results in Junit Xml format", required = false, showDefaultValue = Visibility.ALWAYS)
	private String junitXml = null;

	@Option(names = {
			"--csv" }, description = "Write test results in csv format", required = false, showDefaultValue = Visibility.ALWAYS)
	private String csv = null;

	@Option(names = { "--update-snapshot",
			"--updateSnapshot" }, description = "Use this flag to re-record every snapshot that fails during this test run.", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean updateSnapshot = false;

	@Option(names = { "--ci" }, description = "Activates CI mode. Instead of automatically storing a new snapshot as per usual, it will now fail the test.", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean ciMode = false;

	@Option(names = { "--related-tests", "--relatedTests"}, description = "Finds and executes all related tests for the provided .nf or nf.test files.", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean findRelatedTests = false;

	@Option(names = { "--follow-dependencies", "--followDependencies"}, description = "Follows all dependencies when related-tests is set.", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean followDependencies = false;

	@Option(names = { "--filter" }, description =  "Filter test cases by specified types (e.g., module, pipeline, workflow or function). Multiple types can be separated by commas.", required = false, showDefaultValue = Visibility.ALWAYS)
	private String dependencies = "all";

	@Option(names = { "--only-changed", "--onlyChanged"}, description = "Runs tests only for those files which are modified in the current git repository", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean onlyChanged = false;

	@Option(names = { "--changed-since", "--changedSince"}, description = "Runs tests related to the changes since the provided branch or commit hash", required = false, showDefaultValue = Visibility.ALWAYS)
	private String changedSince = null;

	@Option(names = { "--changed-until", "--changedUntil"}, description = "Runs tests related to the changes until the provided branch or commit hash", required = false, showDefaultValue = Visibility.ALWAYS)
	private String changedUntil = null;

	@Option(names = { "--coverage"}, description = "Print simple coverage calculation.", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean coverage = false;

	@Option(names = { "--dry-run", "--dryRun" }, description = "Execute most of test discovery but stop before running any of the testcases.", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean dryRun = false;

	@Option(names = {
			"--graph" }, description = "Export dependency graph as dot file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String graph = null;

	@Option(names = { "--clean-snapshot", "--cleanSnapshot", "--wipe-snapshot",
			"--wipeSnapshot" }, description = "Removes all obsolete snapshots.", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean cleanSnapshot = false;

	@Option(names = {
			"--shard" }, description = "Split into arbitrary chunks. Format: i/n. Example: 2/5 executes the second chunk of five.", required = false, showDefaultValue = Visibility.ALWAYS)
	private String shard = null;

	@Option(names = {
			"--shard-strategy" }, description = "Strategy to build shards. Values: round-robin or none.", required = false, showDefaultValue = Visibility.ALWAYS)
	private String shardStrategy = SHARD_STRATEGY_ROUND_ROBIN;

	@Option(names = {
			"--lib" }, description = "Library extension path", required = false, showDefaultValue = Visibility.ALWAYS)
	private String lib = "";

	@Option(names = { "--config",
			"-c" }, description = "nf-test.config filename", required = false, showDefaultValue = Visibility.ALWAYS)

	private String configFilename = Config.FILENAME;

	@Option(names = {
			"--plugins" }, description = "Library extension path", required = false, showDefaultValue = Visibility.ALWAYS)
	private String plugins = null;

	@Option(names = {
			"--tag" }, split = ",", description = "Execute only tests with this tag", required = false, showDefaultValue = Visibility.ALWAYS)
	private List<String> tags = new Vector<String>();

	private static Logger log = LoggerFactory.getLogger(RunTestsCommand.class);

	@Override
	public Integer execute() throws Exception {

		List<File> scripts = new ArrayList<File>();
		Config config = null;
		PluginManager manager = new PluginManager(false);

		try {

			File defaultConfigFile = null;
			String libDir = lib;
			boolean defaultWithTrace = true;
			try {
				File configFile = new File(configFilename);
				if (configFile.exists()) {
					log.info("Load config from file {}...", configFile.getAbsolutePath());
					config = Config.parse(configFile);
					defaultConfigFile = config.getConfigFile();
					defaultWithTrace = config.isWithTrace();
					if (!libDir.isEmpty()) {
						libDir += ":";
					}
					libDir += config.getLibDir();
					manager = config.getPluginManager();

					if (testPaths.isEmpty()) {
						File folder = new File(config.getTestsDir());
						testPaths.add(folder);
					}

					TestSuiteBuilder.setConfig(config);

				} else {
					TestSuiteBuilder.setConfig(null);
					System.out.println(AnsiColors.yellow("Warning: This pipeline has no nf-test config file."));
					log.warn("No nf-test config file found.");
				}

			} catch (Exception e) {

				System.out.println(AnsiColors.red("Error: Syntax errors in nf-test config file: " + e));
				log.error("Parsing config file failed", e);
				if (debug) {
					e.printStackTrace();
				}
				return 2;

			}

			if ((onlyChanged || findRelatedTests || changedSince != null) && config == null) {
				System.out.println(AnsiColors.red("To find related tests a nf-test config file has to be present."));
				return 2;
			}

			File baseDir = new File(new File("").getAbsolutePath());
			DependencyResolver resolver = new DependencyResolver(baseDir);
			resolver.setFollowingDependencies(followDependencies);
			resolver.setTargets(IMetaFile.TargetType.parse(dependencies));

			if (onlyChanged || changedSince != null) {

				GitCommand git = new GitCommand();
				List<File> changedFiles = null;

				if (onlyChanged) {
					changedFiles = git.findChanges(baseDir);
				}else if(changedSince != null && changedUntil == null) {
					changedFiles = git.findChangesSince(baseDir, changedSince);
				} else if(changedSince != null && changedUntil != null) {
						changedFiles = git.findChangesBetween(baseDir, changedSince, changedUntil);
				}

				if (changedFiles.isEmpty()) {
					System.out.println(AnsiColors.green("Nothing to do."));
					return 0;
				} else {
					System.out.println("Detected " + changedFiles.size() + " changed files");
					AnsiText.printBulletList(changedFiles);
				}

				testPaths = changedFiles;
				findRelatedTests = true;
			}

			if (findRelatedTests) {

				resolver.buildGraph(config.getIgnore(), config.getTriggers());
				scripts = resolver.findRelatedTestsByFiles(testPaths);
				System.out.println("Found " + scripts.size() + " related test(s)");
				if (scripts.isEmpty()) {
					System.out.println(AnsiColors.green("Nothing to do."));
					return 0;
				}

				AnsiText.printBulletList(scripts);

			} else {
				if (config != null) {
					resolver.buildGraph(config.getIgnore(), config.getTriggers());
				} else {
					resolver.buildGraph();
				}
				scripts = resolver.findTestsByFiles(testPaths);
			}

			if (graph != null) {
				DependencyExporter.generateDotFile(resolver, graph);
			}


			if (scripts.isEmpty()) {
				System.out.println(AnsiColors
						.yellow("No tests to execute."));
				log.warn("No tests or directories found containing test files. Or all testcases were filtered.");
				return 0;
			} else {
				log.info("Detected {} test files.", scripts.size());
			}

			loadPlugins(manager, plugins);

			GroupTestExecutionListener listener = setupExecutionListeners();

			NextflowCommand.setVerbose(verbose);

			Environment environment = new Environment();
			environment.setLibDir(libDir);
			environment.setPluginManager(manager);

			TestSuiteResolver testSuiteResolver = new TestSuiteResolver(environment);
			List<ITestSuite> testSuits = testSuiteResolver.parse(scripts, new TagQuery(tags));

			testSuits.sort(TestSuiteSorter.getDefault());
			if (shard != null) {
				if (shardStrategy.equalsIgnoreCase(SHARD_STRATEGY_ROUND_ROBIN)){
					testSuits = TestSuiteSharder.shardWithRoundRobin(testSuits, shard);
				} else {
					testSuits = TestSuiteSharder.shard(testSuits, shard);
				}
			}

			TestExecutionEngine engine = new TestExecutionEngine();
			engine.setListener(listener);
			engine.setTestSuites(testSuits);
			engine.setDebug(debug);
			engine.setUpdateSnapshot(updateSnapshot);
			engine.setCleanSnapshot(cleanSnapshot);
			engine.setCIMode(ciMode);
			engine.addProfile(profile);
			engine.setDryRun(dryRun);
			if (withoutTrace) {
				engine.setWithTrace(false);
			} else {
				engine.setWithTrace(defaultWithTrace);
			}

			engine.setConfigFile(defaultConfigFile);

			if (dryRun) {
				System.out.println(AnsiColors.yellow("Dry run mode activated: tests are not executed, just listed."));
			}

			int exitStatus = engine.execute();

			if (coverage && findRelatedTests) {
				new Coverage(resolver).getByFiles(testPaths).print();
			} else if (coverage) {
				new Coverage(resolver).getAll().print();
			}
			System.out.println("The exit status is: " + exitStatus);
			return exitStatus;

		} catch (Throwable e) {
			System.out.println(AnsiColors.red("IN RUNNING ERRORS"));
			System.out.println(AnsiColors.red("Error: " + e));
			log.error("Running tests failed.", e);

			if (debug) {
				e.printStackTrace();
			}

			return 1;

		}

	}

	private GroupTestExecutionListener setupExecutionListeners() throws IOException {
		GroupTestExecutionListener listener = new GroupTestExecutionListener();
		listener.addListener(new AnsiTestExecutionListener());
		if (tap != null) {
			listener.addListener(new TapTestReportWriter(tap));
		}

		if (junitXml != null) {
			listener.addListener(new XmlReportWriter(junitXml));
		}

		if (csv != null) {
			listener.addListener(new CsvReportWriter(csv));
		}
		return listener;
	}

	private void loadPlugins(PluginManager manager, String plugins) throws IOException {

		if (plugins == null) {
			return;
		}

		for (String plugin : plugins.split("\\:")) {
			if (plugin.endsWith(".jar")) {
				manager.loadFromFile(plugin);
			} else {
				manager.load(plugin);
			}

		}
	}

	public static boolean isSupportedFile(Path path) {
		return path.getFileName().toString().endsWith(".nf.test")
				|| path.getFileName().toString().endsWith(".groovy.test");
	}

	public static List<File> findTests(File folder) throws Exception {

		final List<File> scripts = new Vector<File>();

		if (!folder.exists()) {
			throw new Exception("Test directory '" + folder.getAbsolutePath() + "' not found.");
		}

		Files.walk(Paths.get(folder.getAbsolutePath())).forEach(new Consumer<Path>() {

			@Override
			public void accept(Path path) {
				if (Files.isRegularFile(path) && isSupportedFile(path)) {
					scripts.add(path.toFile());
				}
			}

		});
		return scripts;
	}

	public static List<File> pathsToScripts(List<File> paths) throws Exception {
		List<File> scripts = new ArrayList<File>();
		for (File path : paths) {
			if (path.isDirectory()) {
				scripts.addAll(findTests(path));
			} else {
				scripts.add(path);
			}
			;
		}
		;
		return scripts;
	}
}
