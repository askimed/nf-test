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

public class FunctionTestGenerator implements ITestGenerator {

	public static final String TEMPLATE = "FunctionTestTemplate.nf.test";

	public int generate(File source, File target) throws Exception {

		System.out.println("Load source file '" + source.getAbsolutePath() + "'");

		NextflowScript script = new NextflowScript(source);
		script.load();

		if (script.getFunctions().isEmpty()) {
			System.out.println(AnsiColors.yellow("Skipped. No function definition found."));
			return 0;
		} else {
			System.out.println("Found " + script.getFunctions().size() + " functions: " + script.getFunctions());
		}

		if (target.exists()) {
			System.out.println(
					AnsiColors.yellow("Skipped. Test file '" + target.getAbsolutePath() + "' already exists."));
			return 0;
		}

		Map<Object, Object> binding = new HashMap<Object, Object>();
		binding.put("name", source.getName());
		binding.put("script", source.getPath());
		binding.put("functions", script.getFunctions());

		URL templateUrl = FunctionTestGenerator.class.getResource(TEMPLATE);
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);

		File parent = target.getParentFile();
		FileUtil.createDirectory(parent);

		FileUtil.write(target, template);

		System.out.println(AnsiColors.green("Wrote function test file '" + target.getAbsolutePath() + ""));

		return 1;

	}

}
