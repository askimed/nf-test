package com.askimed.nf.test.util;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class MapUtil {

	public static TreeMap<String, Object> convertToTreeMap(Map<String, Object> hashMap) {
		TreeMap<String, Object> treeMap = new TreeMap<>();
		convertToTreeMap(hashMap, treeMap);
		return treeMap;
	}

    @SuppressWarnings("unchecked")
	private static void convertToTreeMap(Map<String, Object> sourceMap, TreeMap<String, Object> targetMap) {
		for (Map.Entry<String, Object> entry : sourceMap.entrySet()) {
			String currentKey = entry.getKey();

			if (entry.getValue() instanceof Map) {
				// nested map
				TreeMap<String, Object> nestedTreeMap = new TreeMap<>();
				targetMap.put(currentKey, nestedTreeMap);
				convertToTreeMap((Map<String, Object>) entry.getValue(), nestedTreeMap);
			} else if (entry.getValue() instanceof List) {
				// and array
				List<Object> targetList = new Vector<Object>();
				for (Object item : (List<Object>) entry.getValue()) {
					// nested map in array
					if (item instanceof Map) {
						TreeMap<String, Object> treeMap = new TreeMap<>();
						convertToTreeMap((Map<String, Object>) item, treeMap);
						targetList.add(treeMap);
					} else {
						targetList.add(item);
					}
				}
				targetMap.put(currentKey, targetList);
			} else {
				targetMap.put(currentKey, entry.getValue());
			}
		}
	}
	
}
