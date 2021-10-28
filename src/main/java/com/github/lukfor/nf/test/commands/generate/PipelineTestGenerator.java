package com.github.lukfor.nf.test.commands.generate;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.github.lukfor.nf.test.util.AnsiColors;
import com.github.lukfor.nf.test.util.FileUtil;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class PipelineTestGenerator implements ITestGenerator {

	public static final String TEMPLATE = "PipelineTestTemplate.nf.test";

	public boolean generate(File source, File target) throws Exception {

		System.out.println("Load source file '" + source.getAbsolutePath() + "'");

		if (target.exists()) {
			System.out.println(
					AnsiColors.yellow("Skipped. Test file '" + target.getAbsolutePath() + "' already exists."));
			return false;
		}

		String name = source.getName();
		String script = source.getPath();

		Map<Object, Object> binding = new HashMap<Object, Object>();
		binding.put("name", name);
		binding.put("script", script);

		URL templateUrl = PipelineTestGenerator.class.getResource(TEMPLATE);
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);

		File parent = target.getParentFile();
		FileUtil.createDirectory(parent);

		FileUtil.write(target, template);

		System.out.println(AnsiColors.green("Wrote pipeline test file '" + target.getAbsolutePath() + ""));

		return true;

	}

}
