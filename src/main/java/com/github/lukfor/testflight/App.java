package com.github.lukfor.testflight;

import com.github.lukfor.testflight.core.TestExecutionEngine;
import com.github.lukfor.testflight.util.AnsiText;
import com.github.lukfor.testflight.util.Emoji;

public class App {

	public static final String VERSION = "0.1.0";

	public int run(String[] args) {

		try {

			printHeader();

			TestExecutionEngine engine = new TestExecutionEngine();
			engine.setScripts(args);
			int exitCode = engine.execute();
			return exitCode;

		} catch (Throwable e) {

			System.out.println("Error: ");
			e.printStackTrace();
			return 1;

		}

	}

	private void printHeader() {

		System.out.println();
		System.out.println(Emoji.ROCKET + AnsiText.bold(" nf-testflight " + App.VERSION));
		System.out.println("https://github.com/lukfor/nf-testflight");
		System.out.println("(c) 2021 Lukas Forer amd Sebastian Schoenherr");
		System.out.println();

	}

	public static void main(String[] args) throws Exception {

		App app = new App();
		int exitCode = app.run(args);
		System.exit(exitCode);

	}

}
