package com.askimed.nf.test.commands.generate;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.askimed.nf.test.nextflow.NextflowScript;
import com.askimed.nf.test.util.AnsiColors;
import com.askimed.nf.test.util.FileUtil;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class ProcessTestGenerator implements ITestGenerator {

	public static final String TEMPLATE = "ProcessTestTemplate.nf.test";

	public int generate(File source, File target) throws Exception {

		System.out.println("Load source file '" + source.getAbsolutePath() + "'");

		NextflowScript script = new NextflowScript(source);
		script.load();

		if (script.getProcesses().isEmpty()) {
			System.out.println(AnsiColors.yellow("Skipped. No process definition found."));
			return 0;
		}


		File[] targets = new File[script.getProcesses().size()];
		if (script.getProcesses().size() == 1) {
			targets[0] = target;
		} else {
			for (int i = 0; i < script.getProcesses().size(); i++) {
				String name = script.getProcesses().get(i).toLowerCase();
				targets[i] = new File(target.getPath().replaceAll(".nf.test", "." + name + ".nf.test"));
			}
		}

		int created = 0;
		for (int i = 0; i < script.getProcesses().size(); i++) {

			if (targets[i].exists()) {
				System.out.println(
						AnsiColors.yellow("Skipped. Test file '" + targets[i].getAbsolutePath() + "' already exists."));
				break;
			}

			Map<Object, Object> binding = new HashMap<Object, Object>();
			binding.put("name", source.getName());
			binding.put("script", source.getPath());
			binding.put("process", script.getProcesses().get(i));

			URL templateUrl = ProcessTestGenerator.class.getResource(TEMPLATE);
			SimpleTemplateEngine engine = new SimpleTemplateEngine();
			Writable template = engine.createTemplate(templateUrl).make(binding);

			File parent = targets[i].getParentFile();
			FileUtil.createDirectory(parent);

			FileUtil.write(targets[i], template);

			System.out.println(AnsiColors.green("Wrote process test file '" + targets[i].getAbsolutePath() + ""));
			created++;
		}
		return created;

	}

}
