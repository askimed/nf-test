package com.askimed.nf.test.lang;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.codehaus.groovy.control.CompilationFailedException;

import groovy.json.JsonSlurper;
import groovy.lang.Closure;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import groovy.yaml.YamlSlurper;

public class ParamsMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public String baseDir = "";

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

		YamlSlurper yamlSlurper = new YamlSlurper();
		Map<String, Object> map = (Map<String, Object>) yamlSlurper.parse(new FileReader(file));
		map.putAll(this);

		loadFromMap(map);

	}

	public synchronized void loadFromMap(Map<String, Object> map)
			throws CompilationFailedException, ClassNotFoundException, IOException {

		evaluteTemplates(map);
		putAll(map);

	}

	protected synchronized void evaluteTemplates(Map<String, Object> map)
			throws CompilationFailedException, ClassNotFoundException, IOException {

		SimpleTemplateEngine engine = new SimpleTemplateEngine();

		Queue<Map<String, Object>> queue = new LinkedList<Map<String, Object>>();
		queue.add(map);

		while (queue.size() > 0) {

			Map<String, Object> item = queue.remove(); // Pop Item

			for (String key : item.keySet()) {

				Object value = item.get(key);

				if (value instanceof String) {

					String template = value.toString();
					Map<String, Object> binding = new HashMap<String, Object>(map);
					Writable evaluatedTemplate = engine.createTemplate(template).make(binding);
					item.put(key, evaluatedTemplate.toString());

				} else if (value instanceof Map) {

					Map<String, Object> nestedMap = createNestedMap((Map<String, Object>) value);
					queue.add(nestedMap); // Add to queue instead of recurse
					item.put(key, nestedMap);

				} else {

					item.put(key, value);

				}
			}

		}

	}

	public void evaluateNestedClosures() {

		evaluateNestedClosures(this);

	}

	protected void evaluateNestedClosures(Map<String, Object> map) {

		Queue<Map<String, Object>> queue = new LinkedList<Map<String, Object>>();
		queue.add(map);

		while (queue.size() > 0) {

			Map<String, Object> item = queue.remove();

			for (String key : item.keySet()) {
				Object value = item.get(key);

				if (!(value instanceof Closure))
					continue;

				Map<String, Object> nestedMap = createNestedMap();
				Closure closure = (Closure) value;
				closure.setDelegate(nestedMap);
				closure.setResolveStrategy(Closure.DELEGATE_FIRST);
				closure.call();
				item.put(key, nestedMap);

				queue.add(nestedMap); // Instead of recursion

			}

		}

	}

	protected Map<String, Object> createNestedMap() {
		return createNestedMap(null);
	}

	protected Map<String, Object> createNestedMap(Map<String, Object> map) {
		Map<String, Object> nestedMap = new HashMap<String, Object>();
		nestedMap.put("baseDir", baseDir);
		nestedMap.put("outputDir", outputDir);
		if (map != null) {
			nestedMap.putAll(map);
		}
		return nestedMap;
	}

}
