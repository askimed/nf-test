package com.askimed.nf.test.util;

import java.util.*;

public class MapTraverser {

    public static void traverse(String prefix, Object root, MapOperation operation) {
        traverseRecursive(root, prefix, operation);
    }

    private static void traverseRecursive(Object node, String path, MapOperation operation) {
        if (node instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) node;
            for (Map.Entry<String, Object> entry : new HashSet<>(map.entrySet())) {
                String key = entry.getKey();
                Object value = entry.getValue();
                String currentPath = path.isEmpty() ? key : path + "." + key;
                traverseRecursive(value, currentPath, operation);
                if (!(value instanceof Map) && !(value instanceof List)) {
                    Object newValue = operation.map(currentPath, value);
                    if (newValue != null) {
                        map.put(key, newValue);
                    }
                }
            }
        } else if (node instanceof List) {
            List<Object> list = (List<Object>) node;
            for (int i = 0; i < list.size(); i++) {
                Object value = list.get(i);
                String currentPath = path + "[" + i + "]";
                traverseRecursive(value, currentPath, operation);
                if (!(value instanceof Map) && !(value instanceof List)) {
                    Object newValue = operation.map(currentPath, value);
                    if (newValue != null) {
                        list.set(i, newValue);
                    }
                }
            }
        }
    }
}
