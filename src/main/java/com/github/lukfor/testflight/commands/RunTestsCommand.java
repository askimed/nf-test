package com.github.lukfor.testflight.commands;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import com.github.lukfor.testflight.core.TestExecutionEngine;
import com.github.lukfor.testflight.util.AnsiColors;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "test")
public class RunTestsCommand implements Callable<Integer> {

	@Parameters(description = "test files")
	List<File> scripts;

	@Override
	public Integer call() throws Exception {

		try {

			TestExecutionEngine engine = new TestExecutionEngine();
			engine.setScripts(scripts);
			return engine.execute();

		} catch (Throwable e) {

			System.out.println(AnsiColors.red("Error: " + e));
			return 1;

		}

	}

}
