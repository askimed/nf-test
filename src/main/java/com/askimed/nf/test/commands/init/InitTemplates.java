package com.askimed.nf.test.commands.init;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.util.FileUtil;

import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class InitTemplates {

	public static final String TEMPLATE_CONFIG = "nf-test.config";
	
	public static final String TEMPLATE_NEXTFLOW_CONFIG = "nextflow.config";
	
	public static void createConfig(File file) throws IOException, CompilationFailedException, ClassNotFoundException {
		
		Map<Object, Object> binding = new HashMap<Object, Object>();
		
		URL templateUrl = InitTemplates.class.getResource(TEMPLATE_CONFIG);
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);
		
		FileUtil.write(file, template);
		
	}
	
	
	public static void createNextflowConfig(File file) throws IOException, CompilationFailedException, ClassNotFoundException {
		
		Map<Object, Object> binding = new HashMap<Object, Object>();
		
		URL templateUrl = InitTemplates.class.getResource(TEMPLATE_NEXTFLOW_CONFIG);
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);
		
		file.getParentFile().mkdirs();
		
		FileUtil.write(file, template);
		
	}
	
}
