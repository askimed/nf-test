package com.askimed.nf.test.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.core.AnsiTestExecutionListener;
import com.askimed.nf.test.core.GroupTestExecutionListener;
import com.askimed.nf.test.core.TagQuery;
import com.askimed.nf.test.core.TestExecutionEngine;
import com.askimed.nf.test.core.reports.TapTestReportWriter;
import com.askimed.nf.test.core.reports.XmlReportWriter;
import com.askimed.nf.test.lang.TestSuiteBuilder;
import com.askimed.nf.test.plugins.PluginManager;
import com.askimed.nf.test.util.AnsiColors;

import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "test")
public class RunTestsCommand extends AbstractCommand {

	@Parameters(description = "test files")
	private List<File> testPaths = new ArrayList<File>();

	@Option(names = {
			"--debug" }, description = "Show Nextflow output", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean debug = false;

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
			"--junitxml" }, description = "Write test results in Junit Xml Format", required = false, showDefaultValue = Visibility.ALWAYS)
	private String junitXml = null;

	@Option(names = { "--update-snapshot",
			"--updateSnapshot" }, description = "Use this flag to re-record every snapshot that fails during this test run.", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean updateSnapshot = false;
	@Option(names = {
			"--lib" }, description = "Library extension path", required = false, showDefaultValue = Visibility.ALWAYS)
	private String lib = "";

	@Option(names = {
			"--plugins" }, description = "Library extension path", required = false, showDefaultValue = Visibility.ALWAYS)
	private String plugins = null;

	@Option(names = {
			"--tag" }, split = ",", description = "Execute only tests with this tag", required = false, showDefaultValue = Visibility.ALWAYS)
	private List<String> tags = new Vector<String>();

	@Override
	public Integer execute() throws Exception {

		List<File> scripts = new ArrayList<File>();
		PluginManager manager = new PluginManager(false);

		try {

			String defaultProfile = null;
			File defaultConfigFile = null;
			String libDir = lib;
			boolean defaultWithTrace = true;
			try {

				File configFile = new File(Config.FILENAME);
				if (configFile.exists()) {

					Config config = Config.parse(configFile);
					defaultProfile = config.getProfile();
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
				}

				scripts = pathsToScripts(testPaths);

			} catch (Exception e) {

				System.out.println(AnsiColors.red("Error: Syntax errors in nf-test config file: " + e));
				if (debug) {
					e.printStackTrace();
				}
				return 2;

			}

			if (scripts.size() == 0) {
				System.out.println(AnsiColors
						.red("Error: No tests or test directories containing scripts that end with *.test provided."));
				return 2;
			}

			loadPlugins(manager, plugins);

			GroupTestExecutionListener listener = new GroupTestExecutionListener();
			listener.addListener(new AnsiTestExecutionListener());
			if (tap != null) {
				listener.addListener(new TapTestReportWriter(tap));
			}

			if (junitXml != null) {
				listener.addListener(new XmlReportWriter(junitXml));
			}

			TagQuery tagQuery = new TagQuery(tags);

			TestExecutionEngine engine = new TestExecutionEngine();
			engine.setListener(listener);
			engine.setScripts(scripts);
			engine.setTagQuery(tagQuery);
			engine.setDebug(debug);
			engine.setUpdateSnapshot(updateSnapshot);
			engine.setLibDir(libDir);
			engine.setPluginManager(manager);

			if (profile != null) {
				engine.setProfile(profile);
			} else {
				engine.setProfile(defaultProfile);
			}
			if (withoutTrace) {
				engine.setWithTrace(false);
			} else {
				engine.setWithTrace(defaultWithTrace);
			}

			engine.setConfigFile(defaultConfigFile);
			return engine.execute();

		} catch (Throwable e) {

			System.out.println(AnsiColors.red("Error: " + e));

			if (debug) {
				e.printStackTrace();
			}

			return 1;

		}

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
