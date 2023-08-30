package com.askimed.nf.test.lang.channels;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.MapUtil;

import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

public class Channels extends TreeMap<Object, Object> {

	private static final String OUTPUT_CHANNEL_PREFIX = "output_";

	private static final long serialVersionUID = 1L;

	public void loadFromFolder(File folder, boolean autoSort) {
		for (File file : folder.listFiles()) {
			if (file.getName().startsWith(OUTPUT_CHANNEL_PREFIX)) {
				loadFromFile(file, autoSort);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void loadFromFile(File file, boolean autoSort) {
		JsonSlurper jsonSlurper = new JsonSlurper();
		Map<String, Object> map = (Map<String, Object>) jsonSlurper.parse(file);
		//convert map to treemap to sort keys of nested objects
		map = MapUtil.convertToTreeMap(map);
		if (autoSort) {
			for (Object key : map.keySet()) {
				Object value = map.get(key);
				List<Object> channel = (List<Object>) value;
				sortChannel(channel);
			}
		}
		putAll(map);
	}

	public void view() {
		System.out.println(AnsiText.padding(JsonOutput.prettyPrint(JsonOutput.toJson(this)), 4));
	}

	public void sortChannel(List<Object> channel) {
		channel.sort(new ChannelItemComparator());
	}

	@Override
	public Object get(Object key) {
		return super.get(key.toString());
	}
}
