package com.askimed.nf.test.commands;

import java.util.Arrays;
import java.util.concurrent.Callable;

import com.askimed.nf.test.nextflow.NextflowCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.askimed.nf.test.App;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.Emoji;
import com.askimed.nf.test.util.LoggerUtil;

import picocli.CommandLine.Option;
import picocli.CommandLine.Help.Visibility;

public abstract class AbstractCommand implements Callable<Integer> {

	@Option(names = {
			"--silent" }, description = "Hide header and program version", required = false, showDefaultValue = Visibility.ALWAYS)
	private boolean silent = false;

	@Option(names = {
			"--log" }, description = "Filename for log file", required = false, showDefaultValue = Visibility.ALWAYS)
	private String logFilename = App.LOG_FILENAME;

	private static Logger log = LoggerFactory.getLogger(App.class);

	@Override
	public Integer call() throws Exception {

		LoggerUtil.init(App.PACKAGE, logFilename, App.LOG_LEVEL);

		log.info(App.NAME + " " + App.VERSION);
		log.info("Arguments: " + Arrays.toString(App.args));
		log.info("Nextflow Version: " + NextflowCommand.getVersion());

		if (!silent) {
			printHeader();
		}

		return execute();
	}

	public abstract Integer execute() throws Exception;

	private void printHeader() {

		System.out.println();
		System.out.println(Emoji.ROCKET + AnsiText.bold(" " + App.NAME + " " + App.VERSION));
		System.out.println("https://www.nf-test.com");
		System.out.println("Please cite: https://doi.org/10.1093/gigascience/giaf130");
		System.out.println("(c) 2021 - 2026 Lukas Forer and Sebastian Schoenherr");
		System.out.println();

	}

}
