package com.github.lukfor.testflight.commands;

import java.io.File;
import java.util.concurrent.Callable;

import com.github.lukfor.testflight.App;
import com.github.lukfor.testflight.commands.init.ConfigTemplate;
import com.github.lukfor.testflight.config.Config;
import com.github.lukfor.testflight.util.AnsiColors;

import picocli.CommandLine.Command;

@Command(name = "init")
public class InitCommand implements Callable<Integer> {

	@Override
	public Integer call() throws Exception {

		try {

			File file = new File(Config.FILENAME);

			if (file.exists()) {
				System.out.println(AnsiColors.red("Error:" + App.NAME + " is already setup for this project."));
				return 1;
			}

			ConfigTemplate.create(file);

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
