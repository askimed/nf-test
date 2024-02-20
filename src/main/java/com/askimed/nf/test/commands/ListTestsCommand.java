package com.askimed.nf.test.commands;

import java.io.File;
import java.util.*;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.core.*;
import com.askimed.nf.test.lang.dependencies.DependencyResolver;
import com.askimed.nf.test.util.AnsiColors;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.FileUtil;
import com.askimed.nf.test.util.OutputFormat;

import groovy.json.JsonOutput;
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

	@Option(names = {
			"--format" }, description = "Output format", required = false, showDefaultValue = Visibility.ALWAYS)
	private OutputFormat format = OutputFormat.PRETTY;

	@Override
	public Integer execute() throws Exception {

		try {

			Config config = null;

			try {

				File configFile = new File(Config.FILENAME);

				if (configFile.exists()) {

					config = Config.parse(configFile);

					if (testPaths.isEmpty()) {
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

			DependencyResolver resolver = new DependencyResolver(new File(new File("").getAbsolutePath()));
			if (config != null) {
				resolver.buildGraph(config.getIgnore());
			} else {
				resolver.buildGraph();
			}

			List<File> scripts = resolver.findTestsByFiles(testPaths);

			if (scripts.isEmpty()) {
				System.out.println(AnsiColors.red("Error: No tests provided and no test directory set."));
				return 2;
			}

			Environment environment = new Environment();

			TestSuiteResolver testSuiteResolver = new TestSuiteResolver(environment);
			List<ITestSuite> testSuits = testSuiteResolver.parse(scripts);

			if (testSuits.isEmpty()) {
				System.out.println(AnsiColors.red("Error: no valid tests found."));
				System.out.println();
				return 1;
			}

			if (tags) {
				return listTags(testSuits, format);
			} else {
				return listTests(testSuits, format);
			}

		} catch (Throwable e) {

			System.out.println(AnsiColors.red("Error: " + e));

			return 1;

		}

	}


	public int listTests(List<ITestSuite> testSuits, OutputFormat format) throws Throwable {

		switch (format) {
			case JSON:
			case json:
				printTestsAsJson(testSuits);
				break;
			case RAW:
			case raw:
				printTestsAsList(testSuits);
				break;
			case CSV:
			case csv:
				printTestsAsCsv(testSuits);
				break;
			default:
				printTestsPretty(testSuits);
				break;
		}

		return 0;

	}

	public int listTags(List<ITestSuite> testSuits, OutputFormat format) throws Throwable {

		Set<String> tags = new HashSet<String>();
		for (ITestSuite testSuite : testSuits) {
			tags.addAll(testSuite.getTags());
			for (ITest test : testSuite.getTests()) {
				tags.addAll(test.getTags());
			}
		}

		switch (format) {
			case JSON:
			case json:
				printTagsAsJson(tags);
				break;
			case CSV:
			case csv:
				printTagsAsCsv(tags);
				break;
			default:
				printTagsPretty(tags);
				break;
		}

		return 0;

	}

	private void printTestsAsJson(List<ITestSuite> testSuits) {
		List<String> tests = new Vector<String>();
		for (ITestSuite testSuite : testSuits) {
			for (ITest test : testSuite.getTests()) {
				tests.add(new File(testSuite.getFilename()).getAbsolutePath() + "@" + test.getHash().substring(0, 8));
			}
		}
		System.out.println(JsonOutput.toJson(tests));
	}

	private void printTestsAsList(List<ITestSuite> testSuits) {
		for (ITestSuite testSuite : testSuits) {
			for (ITest test : testSuite.getTests()) {
				System.out.println(new File(testSuite.getFilename()).getAbsolutePath() + "@" + test.getHash().substring(0, 8));
			}
		}
	}

	private void printTestsAsCsv(List<ITestSuite> testSuits) {
		List<String> tests = new Vector<String>();
		for (ITestSuite testSuite : testSuits) {
			for (ITest test : testSuite.getTests()) {
				tests.add(new File(testSuite.getFilename()).getAbsolutePath() + "@" + test.getHash().substring(0, 8));
			}
		}
		System.out.println(String.join(",", tests));
	}

	private void printTestsPretty(List<ITestSuite> testSuits) {
		int count = 0;

		File baseDir = new File("");

		for (ITestSuite testSuite : testSuits) {

			System.out.println();
			System.out.println("[" + FileUtil.makeRelative(baseDir, new File(testSuite.getFilename())) + "] "
					+ AnsiText.bold(testSuite.getName()));
			System.out.println();

			for (ITest test : testSuite.getTests()) {
				System.out.println(AnsiText.padding("[" + FileUtil.makeRelative(baseDir, new File(testSuite.getFilename())) + "@"
						+ test.getHash().substring(0, 8) + "] " + AnsiText.bold(test.getName()), 2));
				count++;
			}
		}

		System.out.println();
		System.out.println("Found " + count + " tests.");
		System.out.println();
	}

	private void printTagsAsJson(Set<String> tags) {
		System.out.println(JsonOutput.toJson(tags));
	}

	private void printTagsAsCsv(Set<String> tags) {
		System.out.println(String.join(",", tags));
	}

	private void printTagsPretty(Set<String> tags) {
		for (String tag : tags) {
			System.out.println(tag);
		}
	}

}
