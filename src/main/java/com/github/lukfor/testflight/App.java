package com.github.lukfor.testflight;

import com.github.lukfor.testflight.commands.RunTestsCommand;
import com.github.lukfor.testflight.util.AnsiText;
import com.github.lukfor.testflight.util.Emoji;

import picocli.CommandLine;

public class App {

	public static final String NAME = "nf-testflight";

	public static final String VERSION = "0.1.0";

	public int run(String[] args) {

		printHeader();

		int exitCode = new CommandLine(new RunTestsCommand()).execute(args);
		return exitCode;

	}

	private void printHeader() {

		System.out.println();
		System.out.println(Emoji.ROCKET + AnsiText.bold(" " + App.NAME + " " + App.VERSION));
		System.out.println("https://github.com/lukfor/nf-testflight");
		System.out.println("(c) 2021 Lukas Forer and Sebastian Schoenherr");
		System.out.println();

	}

	public static void main(String[] args) throws Exception {

		App app = new App();
		int exitCode = app.run(args);
		System.exit(exitCode);

	}

}
