package com.github.lukfor.testflight.commands.init;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.github.lukfor.testflight.util.FileUtil;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class ConfigTemplate {

	public static final String TEMPLATE = "template.config";
	
	public static void create(File file) throws IOException, CompilationFailedException, ClassNotFoundException {
		
		Map<Object, Object> binding = new HashMap<Object, Object>();
		
		URL templateUrl = ConfigTemplate.class.getResource(TEMPLATE);
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);
		
		FileUtil.write(file, template);
		
	}
}
