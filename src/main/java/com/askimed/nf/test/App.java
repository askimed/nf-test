package com.askimed.nf.test;

import com.askimed.nf.test.commands.CleanCommand;
import com.askimed.nf.test.commands.GenerateTestsCommand;
import com.askimed.nf.test.commands.InitCommand;
import com.askimed.nf.test.commands.ListTestsCommand;
import com.askimed.nf.test.commands.RunTestsCommand;
import com.askimed.nf.test.commands.UpdatePluginsCommand;
import com.askimed.nf.test.commands.VersionCommand;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.Emoji;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = App.NAME, version = App.VERSION)
public class App {

	public static final String NAME = "nf-test";

	public static final String VERSION = "0.7.1";

	public int run(String[] args) {

		printHeader();

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

	private void printHeader() {

		System.out.println();
		System.out.println(Emoji.ROCKET + AnsiText.bold(" " + App.NAME + " " + App.VERSION));
		System.out.println("https://code.askimed.com/nf-test");
		System.out.println("(c) 2021 - 2022 Lukas Forer and Sebastian Schoenherr");
		System.out.println();

	}

	public static void main(String[] args) throws Exception {

		App app = new App();
		int exitCode = app.run(args);
		System.exit(exitCode);
	}

}
