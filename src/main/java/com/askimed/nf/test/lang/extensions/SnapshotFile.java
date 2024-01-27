package com.askimed.nf.test.lang.extensions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static Logger log = LoggerFactory.getLogger(SnapshotFile.class);

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
			log.debug("Init new snapshot file '{}'", filename);
			return;
		}
		JsonSlurper jsonSlurper = new JsonSlurper();
		Map<String, Map<String, Object>> map = (Map<String, Map<String, Object>>) jsonSlurper.parse(file);
		for (String id : map.keySet()) {
			Map<String, Object> object = map.get(id);
			String timestamp = object.get("timestamp").toString();
			Object content = object.get("content");
			SnapshotFileItem item = new SnapshotFileItem(timestamp, content);
			if (object.containsKey("meta")) {
				item.setMeta((Map<String, Object>) object.get("meta"));
			}
			snapshots.put(id, item);
		}
		log.debug("Load snapshots from file '{}'", filename);
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
		log.debug("Created snapshot '{}'", id);
	}

	public void updateSnapshot(String id, Object object) {
		updatedSnapshots.add(id);
		snapshots.put(id, new SnapshotFileItem(object));
		log.debug("Updated snapshot '{}'", id);
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
			log.debug("Removed snapshot '{}'", snapshot);
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
		log.debug("Wrote snapshots to file '{}'", filename);
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
