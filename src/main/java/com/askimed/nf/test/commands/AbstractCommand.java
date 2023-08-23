package com.askimed.nf.test.commands;

import java.util.concurrent.Callable;

import com.askimed.nf.test.App;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.Emoji;

import picocli.CommandLine.Option;
import picocli.CommandLine.Help.Visibility;

public abstract class AbstractCommand implements Callable<Integer> {

	@Option(names = {
			"--silent" }, description = "Hide header and program version", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean silent = false;

	@Override
	public Integer call() throws Exception {

		if (!silent) {
			printHeader();
		}

		return execute();
	}

	public abstract Integer execute() throws Exception;

	private void printHeader() {

		System.out.println();
		System.out.println(Emoji.ROCKET + AnsiText.bold(" " + App.NAME + " " + App.VERSION));
		System.out.println("https://code.askimed.com/nf-test");
		System.out.println("(c) 2021 - 2023 Lukas Forer and Sebastian Schoenherr");
		System.out.println();

	}

}
