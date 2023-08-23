package com.askimed.nf.test.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.core.TestExecutionEngine;
import com.askimed.nf.test.util.AnsiColors;

import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "list")
public class ListTestsCommand extends AbstractCommand {

	@Parameters(description = "list all tests")
	private List<File> testPaths = new ArrayList<File>();

	@Option(names = {
			"--debug" }, description = "Show debugging infos", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean debug = false;

	@Option(names = {
			"--tags" }, description = "Show all available tags", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean tags = false;

	@Override
	public Integer execute() throws Exception {

		try {

			try {

				File configFile = new File(Config.FILENAME);

				if (configFile.exists()) {

					Config config = Config.parse(configFile);

					if (testPaths.size() == 0) {
						File folder = new File(config.getTestsDir());
						testPaths.add(folder);
					}

				} else {
					System.out.println(AnsiColors.yellow("Warning: This pipeline has no nf-test config file."));
				}

			} catch (Exception e) {

				System.out.println(AnsiColors.red("Error: Syntax errors in nf-test config file: " + e));
				if (debug) {
					e.printStackTrace();
				}
				return 2;

			}

			List<File> scripts = RunTestsCommand.pathsToScripts(testPaths);

			if (scripts.size() == 0) {
				System.out.println(AnsiColors.red("Error: No tests provided and no test directory set."));
				return 2;
			}

			TestExecutionEngine engine = new TestExecutionEngine();
			engine.setScripts(scripts);
			if (tags) {
				return engine.listTags();
			} else {
				return engine.listTests();
			}

		} catch (Throwable e) {

			System.out.println(AnsiColors.red("Error: " + e));

			return 1;

		}

	}

}
