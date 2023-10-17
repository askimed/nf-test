package com.askimed.nf.test.commands;

import java.io.File;

import com.askimed.nf.test.App;
import com.askimed.nf.test.commands.init.InitTemplates;
import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.util.AnsiColors;

import picocli.CommandLine.Command;

@Command(name = "init")
public class InitCommand extends AbstractCommand {

	@Override
	public Integer execute() throws Exception {

		try {

			File configFile = new File(Config.FILENAME);

			if (configFile.exists()) {
				System.out.println(AnsiColors.red("Error:" + App.NAME + " is already setup for this project."));
				return 1;
			}

			InitTemplates.createConfig(configFile);
			
			
			File nextflowConfigFile = new File(Config.DEFAULT_NEXTFLOW_CONFIG);

			if (nextflowConfigFile.exists()) {
				System.out.println(AnsiColors.yellow("Skipped:" + Config.DEFAULT_NEXTFLOW_CONFIG + " is already setup for this project."));
			} else {
			        InitTemplates.createNextflowConfig(nextflowConfigFile);
			}

			System.out.println(
					AnsiColors.green("Project configured.") + " Configuration is stored in " + Config.FILENAME);
			System.out.println();
			return 0;

		} catch (Throwable e) {

			System.out.println(AnsiColors.red("Error: " + e));
			e.printStackTrace();
			return 1;

		}

	}

}
