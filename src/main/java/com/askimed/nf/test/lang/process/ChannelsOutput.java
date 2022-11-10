package com.askimed.nf.test.lang.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.AnsiColors;

import groovy.json.JsonSlurper;

public class ChannelsOutput extends HashMap<Object, Object> {

	private static final long serialVersionUID = 1L;

	public void loadFromFolder(File folder, boolean autoSort) {
		for (File file : folder.listFiles()) {
			if (file.getName().startsWith("output_")) {
				Map<Object, Object> channel = loadFromFile(file, autoSort);
				putAll(channel);
			}
		}
	}

	public Map<Object, Object> loadFromFile(File file, boolean autoSort) {
		JsonSlurper jsonSlurper = new JsonSlurper();
		Map<Object, Object> map = (Map<Object, Object>) jsonSlurper.parse(file);
		if (autoSort) {
			for (Object key : map.keySet()) {
				Object value = map.get(key);
				List<Object> channel = (List<Object>) value;
				sortChannel(channel);
			}
		}
		return map;
	}

	public void view() {
		System.out
				.println(AnsiText.padding(groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(this)), 4));
	}

	public void sortChannel(List<Object> channel) {
		channel.sort(new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {

				if (o1 instanceof ArrayList) {

					ArrayList<Object> tuple1 = (ArrayList<Object>) o1;
					ArrayList<Object> tuple2 = (ArrayList<Object>) o2;
					for (int i = 0; i < tuple1.size(); i++) {
						Object a = tuple1.get(i);
						Object b = tuple2.get(i);

						int result = compareObjects(a, b);
						
						if (result != 0) {
							return result;
						}

					}
				} else {

					return compareObjects(o1, o2);

				}

				return 0;
			}

		});
	}

	public int compareObjects(Object a, Object b) {

		// TODO: check classA == classB

		if (a.getClass() != b.getClass()) {
			System.err.println(AnsiColors.yellow(
				"\nWarning: Cannot sort channel, order not deterministic. Objects are different types: " 
				+ a.getClass() + " vs. " + b.getClass()
			));
			return 1;
		}

		if (a instanceof String) {
			if (a.toString().startsWith("/")) {
				return comparePaths(a.toString(), b.toString());
			} else {
				return compareStrings(a.toString(), b.toString());
			}
		} else if (a instanceof Integer || a instanceof Double || a instanceof Float) {
			return compareNumbers((Comparable) a, (Comparable) b);
		} else {
			System.err.println(AnsiColors.yellow(
				"\nWarning: Cannot sort channel, order not deterministic. Unsupported objects types: " 
				+ a.getClass() + " vs. " + b.getClass()
			));
			return 1;
		}

	}

	public int comparePaths(String a, String b) {
		String name1 = new File(a).getName();
		String name2 = new File(b).getName();
		return name1.compareTo(name2);
	}

	public int compareStrings(String a, String b) {
		return a.compareTo(b);
	}

	public int compareNumbers(Comparable a, Comparable b) {
		return a.compareTo(b);
	}
}
