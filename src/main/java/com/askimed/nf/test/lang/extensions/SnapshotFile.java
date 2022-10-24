package com.askimed.nf.test.lang.extensions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.askimed.nf.test.core.ITestSuite;

import groovy.json.JsonGenerator;
import groovy.json.JsonGenerator.Converter;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

public class SnapshotFile {

	private String filename;

	private Map<String, SnapshotFileItem> snapshots = new HashMap<String, SnapshotFileItem>();

	public static SnapshotFile loadByTestSuite(ITestSuite suite) {
		String filename = createFilename(suite);
		return new SnapshotFile(filename);
	}

	public static void clearByTestSuite(ITestSuite suite) {
		String filename = createFilename(suite);
		File file = new File(filename);
		if (file.exists()) {
			file.delete();
		}
	}

	public SnapshotFile(String filename) {
		this.filename = filename;
		File file = new File(filename);
		if (!file.exists()) {
			return;
		}
		JsonSlurper jsonSlurper = new JsonSlurper();
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) jsonSlurper.parse(file);
		for (String id : map.keySet()) {
			Map<String, Object> object = map.get(id);
			SnapshotFileItem item = new SnapshotFileItem(new Date(), object.get("content"));
			snapshots.put(id, item);
		}

	}

	public SnapshotFileItem getSnapshot(String id) {
		return snapshots.get(id);
	}

	public void updateSnapshot(String id, Object object) {
		snapshots.put(id, new SnapshotFileItem(new Date(), object));
	}

	public void save() {
		JsonGenerator jsonGenerator = createJsonGenerator();
		String json = jsonGenerator.toJson(snapshots);
		String prettyJson = JsonOutput.prettyPrint(json);
		File file = new File(filename);
		FileWriter writer;
		try {
			writer = new FileWriter(file);
			writer.append(prettyJson);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected static String createFilename(ITestSuite suite) {
		// TODO: create subfolder snapshots? read from config?
		return suite.getFilename() + ".snap";
	}

	public static JsonGenerator createJsonGenerator() {
		JsonGenerator jsonGenerator = new JsonGenerator.Options().excludeFieldsByName("mapping")
				.addConverter(new PathConverter()).build();
		return jsonGenerator;
	}

	static class PathConverter implements Converter {

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

}
