package com.askimed.nf.test.commands;

import com.askimed.nf.test.nextflow.NextflowCommand;
import com.askimed.nf.test.util.AnsiColors;

import picocli.CommandLine.Command;

@Command(name = "version")
public class VersionCommand extends AbstractCommand {

	@Override
	public Integer execute() throws Exception {

		try {

			System.out.println("Nextflow Runtime:");
			NextflowCommand command = new NextflowCommand();
			command.printVersion();

		} catch (Exception e) {

			System.out.println(AnsiColors.red("Error: " + e.getMessage()));

		}

		return 0;
	}

}
