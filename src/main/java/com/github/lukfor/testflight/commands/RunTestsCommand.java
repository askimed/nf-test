package com.github.lukfor.testflight.commands;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import com.github.lukfor.testflight.core.TestExecutionEngine;
import com.github.lukfor.testflight.util.AnsiColors;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
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

			TestExecutionEngine engine = new TestExecutionEngine();
			engine.setScripts(scripts);
			engine.setDebug(debug);
			engine.setProfile(profile);
			return engine.execute();

		} catch (Throwable e) {

			System.out.println(AnsiColors.red("Error: " + e));
			return 1;

		}

	}

}
