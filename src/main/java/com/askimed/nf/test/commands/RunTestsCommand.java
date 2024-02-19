package com.askimed.nf.test.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

import com.askimed.nf.test.core.reports.CsvReportWriter;
import com.askimed.nf.test.lang.dependencies.Coverage;
import com.askimed.nf.test.lang.dependencies.DependencyExporter;
import com.askimed.nf.test.lang.dependencies.DependencyResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.core.AnsiTestExecutionListener;
import com.askimed.nf.test.core.GroupTestExecutionListener;
import com.askimed.nf.test.core.TagQuery;
import com.askimed.nf.test.core.TestExecutionEngine;
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

	@Option(names = { "--related-tests", "--relatedTests"}, description = "Finds all related tests for the provided .nf or nf.test files.", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean findRelatedTests = false;

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

					if (testPaths.size() == 0) {
						File folder = new File(config.getTestsDir());
						testPaths.add(folder);
						System.out.println("Found " + testPaths.size() + " files in test directory.");
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

			List<PathMatcher> ignorePatterns = new Vector<PathMatcher>();

			DependencyResolver resolver = new DependencyResolver(new File(new File("").getAbsolutePath()));

			if (findRelatedTests) {
				if (config == null) {
					System.out.println(AnsiColors
							.red("To find related tests a nf-test config file has to be present."));
					return 2;
				}
				resolver.buildGraph(config.getIgnore());
				scripts = resolver.findRelatedTestsByFiles(testPaths);
				System.out.println("Found " + scripts.size() + " related test(s)");
				if (scripts.isEmpty()) {
					System.out.println(AnsiColors.green("Nothing to do."));
					return 0;
				}
				if (coverage) {
					new Coverage(resolver).getByFiles(testPaths).print();
				}

			} else {
				if (config != null) {
					resolver.buildGraph(config.getIgnore());
				} else {
					resolver.buildGraph();
				}
				scripts = resolver.findTestsByFiles(testPaths);
				if (coverage) {
					new Coverage(resolver).getAll().print();
				}
			}

			if (graph != null) {
				DependencyExporter.generateDotFile(resolver, graph);
			}


			if (scripts.size() == 0) {
				System.out.println(AnsiColors
						.red("Error: No tests or test directories containing scripts that end with *.test provided."));
				log.error("No tests ot directories found containing test files.");
				return 2;
			} else {
				log.info("Detected {} test files.", scripts.size());
			}

			loadPlugins(manager, plugins);

			GroupTestExecutionListener listener = setupExecutionListeners();

			NextflowCommand.setVerbose(verbose);

			//TODO: remove tagQuery from engine. add to resolver? Add simple caching and store to file (hash, tags, name, type, process, ...)
			TagQuery tagQuery = new TagQuery(tags);

			TestExecutionEngine engine = new TestExecutionEngine();
			engine.setListener(listener);
			engine.setScripts(scripts);
			engine.setTagQuery(tagQuery);
			engine.setDebug(debug);
			engine.setUpdateSnapshot(updateSnapshot);
			engine.setCleanSnapshot(cleanSnapshot);
			engine.setLibDir(libDir);
			engine.setPluginManager(manager);
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

			return engine.execute();

		} catch (Throwable e) {

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
