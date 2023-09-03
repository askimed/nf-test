package com.askimed.nf.test;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.askimed.nf.test.commands.CleanCommand;
import com.askimed.nf.test.commands.GenerateTestsCommand;
import com.askimed.nf.test.commands.InitCommand;
import com.askimed.nf.test.commands.ListTestsCommand;
import com.askimed.nf.test.commands.RunTestsCommand;
import com.askimed.nf.test.commands.UpdatePluginsCommand;
import com.askimed.nf.test.commands.VersionCommand;
import com.askimed.nf.test.util.LoggerUtil;

import ch.qos.logback.classic.Level;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = App.NAME, version = App.VERSION)
public class App {

	public static final String NAME = "nf-test";

	public static final String VERSION = "0.7.3";

	public static final String PACKAGE =  "com.askimed.nf.test";
	
	public static final String LOG_FILENAME = ".nf-test.log";

	public static final Level LOG_LEVEL = Level.DEBUG;

	private static Logger log = LoggerFactory.getLogger(App.class);

	public int run(String[] args) {

		//TODO: move LoggerUtil.init(..) to AbstractCommand to use `--log`and `--log-level`
		
		LoggerUtil.init(PACKAGE, LOG_FILENAME, LOG_LEVEL);

		log.info(App.NAME + " " + App.VERSION);
		log.info("Arguments: " + Arrays.toString(args));
		
		CommandLine commandLine = new CommandLine(new App());
		commandLine.addSubcommand("clean", new CleanCommand());
		commandLine.addSubcommand("init", new InitCommand());
		commandLine.addSubcommand("test", new RunTestsCommand());
		commandLine.addSubcommand("list", new ListTestsCommand());
		commandLine.addSubcommand("ls", new ListTestsCommand());
		commandLine.addSubcommand("generate", new GenerateTestsCommand());
		commandLine.addSubcommand("update-plugins", new UpdatePluginsCommand());
		commandLine.addSubcommand("version", new VersionCommand());
		commandLine.setExecutionStrategy(new CommandLine.RunLast());
		return commandLine.execute(args);

	}

	public static void main(String[] args) throws Exception {

		App app = new App();
		int exitCode = app.run(args);
		System.exit(exitCode);
	}

}
