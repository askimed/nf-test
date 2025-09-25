package com.askimed.nf.test.commands;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.lang.dependencies.Coverage;
import com.askimed.nf.test.lang.dependencies.DependencyResolver;
import com.askimed.nf.test.util.AnsiColors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.Option;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Command(name = "status")
public class StatusCommand extends AbstractCommand {

	private static final String SHARD_STRATEGY_ROUND_ROBIN = "round-robin";

	@Option(names = {
			"--csv" }, description = "Write status results in csv format", required = false, showDefaultValue = Visibility.ALWAYS)
	private String csv = null;

	@Option(names = {
			"--html" }, description = "Write status results in html format", required = false, showDefaultValue = Visibility.ALWAYS)
	private String html = null;


	@Option(names = { "--config",
			"-c" }, description = "nf-test.config filename", required = false, showDefaultValue = Visibility.ALWAYS)
	private String configFilename = Config.FILENAME;

	private static Logger log = LoggerFactory.getLogger(StatusCommand.class);

	@Override
	public Integer execute() throws Exception {

		List<File> scripts = new ArrayList<File>();
		Config config = null;

		try {

			File defaultConfigFile = null;
			boolean defaultWithTrace = true;
			try {
				File configFile = new File(configFilename);
				if (configFile.exists()) {
					log.info("Load config from file {}...", configFile.getAbsolutePath());
					config = Config.parse(configFile);
				} else {
					System.out.println(AnsiColors.yellow("Warning: This pipeline has no nf-test config file."));
					log.warn("No nf-test config file found.");
				}

			} catch (Exception e) {

				System.out.println(AnsiColors.red("Error: Syntax errors in nf-test config file: " + e));
				log.error("Parsing config file failed", e);
				return 2;

			}

			File baseDir = new File(new File("").getAbsolutePath());
			DependencyResolver resolver = new DependencyResolver(baseDir);
			resolver.setFollowingDependencies(true);


			if (config != null) {
				resolver.buildGraph(config.getIgnore(), config.getTriggers());
			} else {
				resolver.buildGraph();
			}

			Coverage coverage = new Coverage(resolver).getAll();
			if (csv != null) {
				coverage.exportAsCsv(csv);
			} else if (html != null) {
				coverage.exportAsHtml(html);
			} else {
				coverage.printDetails();
			}

			return 0;

		} catch (Throwable e) {

			System.out.println(AnsiColors.red("Error: " + e));log.error("Running tests failed.", e);
			return 1;

		}

	}

}
