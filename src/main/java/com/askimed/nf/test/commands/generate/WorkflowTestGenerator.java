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

public class WorkflowTestGenerator implements ITestGenerator {

	public static final String TEMPLATE = "WorkflowTestTemplate.nf.test";

	public boolean generate(File source, File target) throws Exception {

		System.out.println("Load source file '" + source.getAbsolutePath() + "'");

		NextflowScript script = new NextflowScript(source);
		script.load();

		if (script.getWorkflows().isEmpty()) {
			System.out.println(AnsiColors.yellow("Skipped. No workflow definition found."));
			return false;
		}

		if (script.getWorkflows().size() > 1) {
			System.out.println(AnsiColors
					.yellow("Skipped. More then one named workflow definition found. Please create one file per process."));
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
		binding.put("workflow", script.getWorkflows().get(0));

		URL templateUrl = WorkflowTestGenerator.class.getResource(TEMPLATE);
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);

		File parent = target.getParentFile();
		FileUtil.createDirectory(parent);

		FileUtil.write(target, template);

		System.out.println(AnsiColors.green("Wrote workflow test file '" + target.getAbsolutePath() + ""));

		return true;
		
	}

}
