package com.askimed.nf.test.commands;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.core.TestExecutionEngine;
import com.askimed.nf.test.util.AnsiColors;

import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "test")
public class RunTestsCommand implements Callable<Integer> {

	@Parameters(description = "test files")
	private List<File> scripts;

	@Option(names = {
			"--debug" }, description = "Show Nextflow output", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean debug = false;

	@Option(names = {
			"--profile" }, description = "Profile to execute all tests", required = false, showDefaultValue = Visibility.ALWAYS)
	private String profile = null;

	@Override
	public Integer call() throws Exception {

		try {

			String defaultProfile = null;
			File defaultConfigFile = null;
			File workDir = new File(".nf-test");

			try {

				Config config = Config.parse(new File(Config.FILENAME));
				defaultProfile = config.getProfile();
				defaultConfigFile = config.getConfigFile();
				workDir = new File(config.getWorkDir());

				if (scripts == null) {
					File folder = new File(config.getTestsDir());
					scripts = findTests(folder);
					System.out.println("Found " + scripts.size() + " files in test directory.");
				}

			} catch (Exception e) {

				System.out.println(AnsiColors.yellow("Warning: This pipeline has no valid nf-test config file."));

				if (debug) {
					e.printStackTrace();
				}

				if (scripts == null) {
					System.out.println(AnsiColors.red("Error: No tests provided and no test directory set."));
					return 2;
				}

			}

			TestExecutionEngine engine = new TestExecutionEngine();
			engine.setScripts(scripts);
			engine.setDebug(debug);
			engine.setWorkDir(workDir);
			if (profile != null) {
				engine.setProfile(profile);
			} else {
				engine.setProfile(defaultProfile);
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

	public static List<File> findTests(File folder) throws Exception {

		final List<File> scripts = new Vector<File>();

		if (!folder.exists()) {
			throw new Exception("Test directory '" + folder.getAbsolutePath() + "' not found.");
		}

		Files.walk(Paths.get(folder.getAbsolutePath())).forEach(new Consumer<Path>() {

			@Override
			public void accept(Path path) {
				if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(".nf.test")) {
					scripts.add(path.toFile());
				}
			}

		});
		return scripts;
	}

}
