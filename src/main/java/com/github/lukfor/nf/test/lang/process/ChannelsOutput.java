package com.github.lukfor.nf.test.lang.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.lukfor.nf.test.util.AnsiText;

import groovy.json.JsonSlurper;

public class ChannelsOutput extends HashMap<Object, Object> {

	private static final long serialVersionUID = 1L;

	public void loadFromFolder(File folder, boolean autoSort) {
		for (File file : folder.listFiles()) {
			Map<Object, Object> channel = loadFromFile(file, autoSort);
			putAll(channel);
		}
	}

	public Map<Object, Object> loadFromFile(File file, boolean autoSort) {
		JsonSlurper jsonSlurper = new JsonSlurper();
		Map<Object, Object> map = (Map<Object, Object>) jsonSlurper.parse(file);
		if (autoSort) {
			for (Object key : map.keySet()) {
				Object value = map.get(key);
				List<Object> channel = (List<Object>) value;
				List<? extends Object> sortedChannel = sortChannel(channel);
				map.put(key, sortedChannel);
			}
		}
		return map;
	}

	public void view() {
		System.out
				.println(AnsiText.padding(groovy.json.JsonOutput.prettyPrint(groovy.json.JsonOutput.toJson(this)), 4));
	}

	public List<? extends Object> sortChannel(List<Object> channel) {
		Class channelClass = detectClassOfChannel(channel);
		if (channelClass == String.class) {
			if (areItemsPaths(channel)) {
				return sortPathChannel(channel);
			} else {
				return sortStringChannel(channel);
			}
		}
		if (channelClass == Integer.class) {
			return sortNumberChannel(channel);
		}
		if (channelClass == Double.class) {
			return sortNumberChannel(channel);
		}
		if (channelClass == Float.class) {
			return sortNumberChannel(channel);
		}
		return channel;
	}

	public List<String> sortStringChannel(List<Object> channel) {
		List<String> newChannel = new ArrayList<String>();
		for (Object item : channel) {
			newChannel.add((String) item);
		}
		Collections.sort(newChannel);
		return newChannel;
	}

	public List<String> sortPathChannel(List<Object> channel) {
		List<String> newChannel = new ArrayList<String>();
		for (Object item : channel) {
			newChannel.add((String) item);
		}
		Collections.sort(newChannel, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				String name1 = new File(o1).getName();
				String name2 = new File(o2).getName();
				return name1.compareTo(name2);
			}
		});
		return newChannel;
	}

	public List<Comparable> sortNumberChannel(List<Object> channel) {
		List<Comparable> newChannel = new ArrayList<Comparable>();
		for (Object item : channel) {
			newChannel.add((Comparable) item);
		}
		Collections.sort(newChannel);
		return newChannel;
	}

	public Class detectClassOfChannel(List<Object> channel) {
		Class clazz = null;
		for (Object item : channel) {
			if (clazz == null) {
				clazz = item.getClass();
			} else {
				if (clazz != item.getClass()) {
					return Object.class;
				}
			}
		}
		return clazz;
	}

	public boolean areItemsPaths(List<Object> channel) {
		for (Object item : channel) {
			if (!item.toString().startsWith("/")) {
				return false;
			}
		}
		return true;
	}

}
