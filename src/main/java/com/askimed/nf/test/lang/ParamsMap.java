package com.askimed.nf.test.lang;

import java.beans.Transient;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.runtime.InvokerHelper;

import groovy.json.JsonSlurper;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.text.SimpleTemplateEngine;
import groovy.yaml.YamlSlurper;

public class ParamsMap extends HashMap<String, Object> implements GroovyObject {

	private static final long serialVersionUID = 1L;

	private TestContext context;

	public ParamsMap(TestContext context) {
		this.context = context;
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

	private String readText(File file) throws IOException, CompilationFailedException, ClassNotFoundException {

		Map<String, String> binding = new HashMap<String, String>();
		binding.put("baseDir", context.baseDir);
		binding.put("launchDir", context.launchDir);
		binding.put("moduleDir", context.moduleDir);
		binding.put("moduleTestDir", context.moduleTestDir);
		binding.put("outputDir", context.outputDir);
		binding.put("projectDir", context.projectDir);

		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		String text = engine.createTemplate(file).make(binding).toString();

		return text;
	}

	public void loadFromJsonFile(File file) throws CompilationFailedException, ClassNotFoundException, IOException {

		JsonSlurper jsonSlurper = new JsonSlurper();
		Map<String, Object> map = (Map<String, Object>) jsonSlurper.parseText(readText(file));
		putAll(map);

	}

	public void loadFromYamlFile(File file) throws CompilationFailedException, ClassNotFoundException, IOException {

		YamlSlurper yamlSlurper = new YamlSlurper();
		Map<String, Object> map = (Map<String, Object>) yamlSlurper.parseText(readText(file));
		putAll(map);

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

				ParamsMap nestedMap = new ParamsMap(context);
				Closure closure = (Closure) value;

				// use context as new owner and this object
				Closure newClosure = closure.rehydrate(nestedMap, context, context);
				newClosure.call();

				item.put(key, nestedMap);

				queue.add(nestedMap); // Instead of recursion

			}

		}

	}

	// never persist the MetaClass
	private transient MetaClass metaClass = getDefaultMetaClass();

	@Override
	@Transient
	public MetaClass getMetaClass() {
		return this.metaClass;
	}

	@Override
	public void setMetaClass(/* @Nullable */ final MetaClass metaClass) {
		this.metaClass = Optional.ofNullable(metaClass).orElseGet(this::getDefaultMetaClass);
	}

	private MetaClass getDefaultMetaClass() {
		return InvokerHelper.getMetaClass(this.getClass());
	}

	public Object invokeMethod(String var1, Object var2) {
		try {
			return this.getMetaClass().invokeMethod(this, var1, var2);
		} catch (groovy.lang.MissingMethodException e) {
			put(var1, ((Object[]) var2)[0]);
			return null;
		}
	}

}
