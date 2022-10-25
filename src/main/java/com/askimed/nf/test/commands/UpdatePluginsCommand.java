package com.askimed.nf.test.commands;

import java.io.File;
import java.util.concurrent.Callable;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.plugins.PluginManager;
import com.askimed.nf.test.util.AnsiColors;

import picocli.CommandLine.Command;

@Command(name = "update-plugins")
public class UpdatePluginsCommand implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {

		File configFile = new File(Config.FILENAME);

		if (configFile.exists()) {

			try {

				PluginManager.FORCE_UPDATE = true;
				Config.parse(configFile);

				System.out.println(AnsiColors.green("Plugins updated.\n"));

				return 0;

			} catch (Exception e) {

				System.out.println(AnsiColors.red("Error: Syntax errors in nf-test config file: " + e));
				return 2;

			}

		} else {

			System.out.println(AnsiColors.red("Error: This pipeline has no nf-test config file."));
			return 2;
		}

	}

}
