package com.askimed.nf.test.commands;

import java.io.File;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.util.AnsiColors;
import com.askimed.nf.test.util.FileUtil;

import picocli.CommandLine.Command;

@Command(name = "clean")
public class CleanCommand extends AbstractCommand {

	public static String NF_DIRECTORY = ".nf-test";

	@Override
	public Integer execute() throws Exception {

		File workDir = new File(NF_DIRECTORY);

		File configFile = new File(Config.FILENAME);

		if (configFile.exists()) {

			try {

				Config config = Config.parse(configFile);
				workDir = new File(config.getWorkDir());

			} catch (Exception e) {

				System.out.println(AnsiColors.red("Error: Syntax errors in nf-test config file: " + e));
				return 2;

			}

		}

		boolean deleted = FileUtil.deleteDirectory(workDir.getAbsoluteFile());

		if (deleted) {

			System.out.println(AnsiColors.green("Working Directory '" + workDir.getAbsolutePath() + "' deleted.\n"));
			return 0;

		} else {

			System.out.println(
					AnsiColors.red("Working Directory '" + workDir.getAbsolutePath() + "' could not be deleted."));
			return 1;

		}

	}

}
