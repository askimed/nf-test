package com.askimed.nf.test.lang.extensions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.askimed.nf.test.core.ITestSuite;
import com.askimed.nf.test.lang.extensions.util.PathConverter;

import groovy.json.JsonGenerator;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

public class SnapshotFile {

	private String filename;

	private Map<String, SnapshotFileItem> snapshots = new HashMap<String, SnapshotFileItem>();

	private Set<String> activeSnapshots = new HashSet<String>();

	private Set<String> createdSnapshots = new HashSet<String>();

	private Set<String> updatedSnapshots = new HashSet<String>();

	private boolean removedSnapshots = false;
	
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
			String timestamp = object.get("timestamp").toString();
			Object content = object.get("content");
			SnapshotFileItem item = new SnapshotFileItem(timestamp, content);
			snapshots.put(id, item);
		}

	}

	public SnapshotFileItem getSnapshot(String id) {
		SnapshotFileItem snapshot = snapshots.get(id);
		if (snapshot != null) {
			activeSnapshots.add(id);
		}
		return snapshot;
	}

	public void createSnapshot(String id, Object object) {
		createdSnapshots.add(id);
		activeSnapshots.add(id);
		snapshots.put(id, new SnapshotFileItem(object));
	}

	public void updateSnapshot(String id, Object object) {
		updatedSnapshots.add(id);
		snapshots.put(id, new SnapshotFileItem(object));
	}

	public Set<String> getCreatedSnapshots() {
		return createdSnapshots;
	}

	public Set<String> getUpdatedSnapshots() {
		return updatedSnapshots;
	}

	public Set<String> getActiveSnapshots() {
		return activeSnapshots;
	}

	public Set<String> getObsoleteSnapshots() {
		Set<String> obsolete = new HashSet<String>(snapshots.keySet());
		obsolete.removeAll(activeSnapshots);
		return obsolete;
	}

	public void removeObsoleteSnapshots() {
		removeSnapshots(getObsoleteSnapshots());
		removedSnapshots = true; 
	}

	public boolean hasRemovedSnapsshots() {
		return removedSnapshots;
	}
	
	private void removeSnapshots(Set<String> obsoleteSnapshots) {
		for (String snapshot : obsoleteSnapshots) {
			snapshots.remove(snapshot);
		}
	}

	public void save() throws IOException {
		JsonGenerator jsonGenerator = createJsonGenerator();
		String json = jsonGenerator.toJson(snapshots);
		String prettyJson = JsonOutput.prettyPrint(json);
		File file = new File(filename);
		FileWriter writer;
		writer = new FileWriter(file);
		writer.append(prettyJson);
		writer.close();
	}

	protected static String createFilename(ITestSuite suite) {
		return suite.getFilename() + ".snap";
	}

	public static JsonGenerator createJsonGenerator() {
		JsonGenerator jsonGenerator = new JsonGenerator.Options().excludeFieldsByName("mapping")
				.addConverter(new PathConverter()).build();
		return jsonGenerator;
	}


}
