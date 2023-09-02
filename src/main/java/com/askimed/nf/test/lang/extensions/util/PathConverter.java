package com.askimed.nf.test.lang.extensions.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.lang.extensions.PathExtension;

import groovy.json.JsonGenerator.Converter;

public class PathConverter implements Converter {

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
		} else {
			path = new File(value.toString()).toPath();

		}

		if (path.toFile().exists()) {
			try {
				if (path.toFile().isFile()) {
					return serializeFile(path);
				} else {
					return serializeDirectory(path);
				}
				// return path.getFileName() + ":base64," + FileUtil.encodeBase64(path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return value;
			}
		}
		return value;
	}

	private String serializeFile(Path path) throws NoSuchAlgorithmException, IOException {
		return path.getFileName() + ":md5," + PathExtension.getMd5(path);
	}

	private List<Object> serializeDirectory(Path folder) throws NoSuchAlgorithmException, IOException {
		List<Object> files = new Vector<Object>();
		for (Path subPath : PathExtension.list(folder)) {
			if (subPath.toFile().isDirectory()) {
				files.add(serializeDirectory(subPath));
			} else {
				files.add(serializeFile(subPath));
			}
		}
		return files;
	}
}