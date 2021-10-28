package com.github.lukfor.nf.test.commands;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import com.github.lukfor.nf.test.commands.generate.ITestGenerator;
import com.github.lukfor.nf.test.commands.generate.ProcessTestGenerator;
import com.github.lukfor.nf.test.commands.generate.WorkflowTestGenerator;
import com.github.lukfor.nf.test.commands.generate.PipelineTestGenerator;
import com.github.lukfor.nf.test.config.Config;
import com.github.lukfor.nf.test.util.AnsiColors;
import com.github.lukfor.nf.test.util.FileUtil;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "generate")
public class GenerateTestsCommand implements Callable<Integer> {

	public static final String TEST_DIRECTORY = "tests";

	@Command(name = "process")
	public Integer process(@Parameters(description = "source files") List<File> scripts) {

		return generate(scripts, new ProcessTestGenerator());

	}

	@Command(name = "workflow")
	public Integer workflow(@Parameters(description = "source files") List<File> scripts) {

		return generate(scripts, new WorkflowTestGenerator());

	}

	@Command(name = "pipeline")
	public Integer pipeline(@Parameters(description = "source files") List<File> scripts) {

		return generate(scripts, new PipelineTestGenerator());

	}

	protected int generate(List<File> scripts, ITestGenerator generator) {

		try {

			Config config = Config.parse(new File(Config.FILENAME));

			int count = 0;

			if (scripts.isEmpty()) {
				System.out.println(AnsiColors.red("Error: No script files provided."));
				return 1;
			}

			for (File script : scripts) {
				if (!script.exists()) {
					System.out.println(AnsiColors.red("Error: script '" + script.getAbsolutePath() + "' not found."));
					return 1;
				}

				String targetPath = FileUtil.path(config.getTestsDir(), script.getPath() + ".test");
				File target = new File(targetPath);

				System.out.println();
				boolean written = generator.generate(script, target);
				if (written) {
					count++;
				}

			}

			System.out.println();
			System.out.println(AnsiColors.green("SUCCESS:") + " Generated " + count + " test files.");
			System.out.println();

			return 0;

		} catch (Throwable e) {

			System.out.println(AnsiColors.red("Error: " + e));
			return 1;

		}
	}

	@Override
	public Integer call() throws Exception {
		System.out.println("Error: Subcommand needed: 'workflow' or 'process' ");
		return 1;
	}

}
