package com.askimed.nf.test.lang.extensions.util;

import com.askimed.nf.test.lang.extensions.PathExtension;
import groovy.json.JsonGenerator.Converter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;

public class PathConverterRaw implements Converter {

	@Override
	public boolean handles(Class<?> type) {
		return true;
	}

	@Override
	public Object convert(Object value, String key) {
		Path path = null;
		if (value instanceof Path) {
			path = (Path) value;
			if (!path.toFile().exists()) {
				throw new RuntimeException("Path " + path.toString() + " not found.");
			}
		} else if (value instanceof File) {
			path = ((File) value).toPath();
			if (!path.toFile().exists()) {
				throw new RuntimeException("Path " + path.toString() + " not found.");
			}
		} else {
			path = new File(value.toString()).toPath();

		}

		if (path.toFile().exists()) {
			return path.toFile().getAbsolutePath();
		}
		return value;
	}

}