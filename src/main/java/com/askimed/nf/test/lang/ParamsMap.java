package com.askimed.nf.test.lang;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;
import org.yaml.snakeyaml.Yaml;

import groovy.json.JsonSlurper;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class ParamsMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public String baseDir = "lukas";

	public String outputDir = "";

	public void setBaseDir(String baseDir) {
		this.baseDir = baseDir;
		put("baseDir", baseDir);
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
		put("outputDir", outputDir);
	}

	public String getBaseDir() {
		return baseDir;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public void load(String filename) throws CompilationFailedException, ClassNotFoundException, IOException {
		load(new File(filename));
	}

	public void load(File file) throws CompilationFailedException, ClassNotFoundException, IOException {

		if (file.getName().endsWith(".json")) {

			loadFromJsonFile(file);

		} else if (file.getName().endsWith(".yaml") || file.getName().endsWith(".yml")) {

			loadFromYamlFile(file);

		} else {

			throw new RuntimeException(
					"Could no load params from file '" + file.getAbsolutePath() + "': Unsupported file format");

		}
	}

	public void loadFromJsonFile(File file) throws CompilationFailedException, ClassNotFoundException, IOException {

		JsonSlurper jsonSlurper = new JsonSlurper();
		Map<String, Object> map = (Map<String, Object>) jsonSlurper.parse(file);
		map.putAll(this);

		loadFromMap(map);
	}

	public void loadFromYamlFile(File file) throws CompilationFailedException, ClassNotFoundException, IOException {

		Yaml parser = new Yaml();
		Map<String, Object> map = (Map<String, Object>) parser.load(new FileReader(file));
		map.putAll(this);

		loadFromMap(map);
	}

	public synchronized void loadFromMap(Map<String, Object> map)
			throws CompilationFailedException, ClassNotFoundException, IOException {

		Map<String, Object> binding = new HashMap<String, Object>(map);

		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		for (String key : map.keySet()) {
			Object value = map.get(key);
			if (value instanceof String) {
				String template = value.toString();
				Writable evaluatedTemplate = engine.createTemplate(template).make(binding);
				map.put(key, evaluatedTemplate.toString());
			} else {
				map.put(key, value);
			}
		}

		putAll(map);
	}

}
