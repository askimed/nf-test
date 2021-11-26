package com.askimed.nf.test.commands;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.core.TestExecutionEngine;
import com.askimed.nf.test.util.AnsiColors;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "list")
public class ListTestsCommand implements Callable<Integer> {

	@Parameters(description = "list all tests")
	private List<File> scripts;

	@Override
	public Integer call() throws Exception {

		try {

			try {

				Config config = Config.parse(new File(Config.FILENAME));
				if (scripts == null) {
					File folder = new File(config.getTestsDir());
					scripts = RunTestsCommand.findTests(folder);
					System.out.println("Found " + scripts.size() + " files in test directory.");
				}

			} catch (Exception e) {

				System.out.println(AnsiColors.yellow("Warning: This pipeline has no valid nf-test config file."));

				if (scripts == null) {
					System.out.println(AnsiColors.red("Error: No tests provided and no test directory set."));
					return 2;
				}

			}

			TestExecutionEngine engine = new TestExecutionEngine();
			engine.setScripts(scripts);
			return engine.listTests();

		} catch (Throwable e) {

			System.out.println(AnsiColors.red("Error: " + e));

			return 1;

		}

	}

}
