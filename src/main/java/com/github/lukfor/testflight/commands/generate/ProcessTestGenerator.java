package com.github.lukfor.testflight.commands.generate;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.github.lukfor.testflight.nextflow.NextflowScript;
import com.github.lukfor.testflight.util.AnsiColors;
import com.github.lukfor.testflight.util.FileUtil;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class ProcessTestGenerator implements ITestGenerator {

	public static final String TEMPLATE = "ProcessTestTemplate.nf.test";

	public boolean generate(File source, File target) throws Exception {

		System.out.println("Load source file '" + source.getAbsolutePath() + "'");

		NextflowScript script = new NextflowScript(source);
		script.load();

		if (script.getProcesses().isEmpty()) {
			System.out.println(AnsiColors.yellow("Skipped. No process definition found."));
			return false;
		}

		if (script.getProcesses().size() > 1) {
			System.out.println(AnsiColors
					.yellow("Skipped. More then one process definition found. Please create one file per process."));
			return false;
		}

		if (target.exists()) {
			System.out.println(
					AnsiColors.yellow("Skipped. Test file '" + target.getAbsolutePath() + "' already exists."));
			return false;
		}

		Map<Object, Object> binding = new HashMap<Object, Object>();
		binding.put("name", source.getName());
		binding.put("script", source.getPath());
		binding.put("process", script.getProcesses().get(0));

		URL templateUrl = ProcessTestGenerator.class.getResource(TEMPLATE);
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);

		File parent = target.getParentFile();
		FileUtil.createDirectory(parent);

		FileUtil.write(target, template);

		System.out.println(AnsiColors.green("Wrote process test file '" + target.getAbsolutePath() + ""));

		return true;
		
	}

}
