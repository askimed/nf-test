package com.askimed.nf.test.lang.extensions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import groovy.json.JsonGenerator;
import groovy.json.JsonOutput;

public class SnapshotFileItem {

	private Object content;

	private String timestamp;

	public static DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

	public SnapshotFileItem(Object content) {
		this.timestamp = createTimestamp();
		this.content = content;
	}

	public SnapshotFileItem(String timestamp, Object content) {
		this.timestamp = timestamp;
		this.content = content;
	}

	public Object getContent() {
		return content;
	}

	public String getTimestamp() {
		return timestamp;
	}

	@Override
	public boolean equals(Object object) {

		if (!(object instanceof SnapshotFileItem)) {
			return false;
		}

		SnapshotFileItem snapshotItem = (SnapshotFileItem) object;

		// At the moment it is easier to compare json output (since md5 hash and
		// filenames)

		if (toString().equals(snapshotItem.toString())) {
			return true;
		}

		throw new RuntimeException(
				"Different Snapshot: \nFound:\n" + toString() + "\n\nExpected:\n" + snapshotItem.toString());

	}

	protected String createTimestamp() {
		return DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
	}

	@Override
	public String toString() {
		JsonGenerator jsonGenerator = SnapshotFile.createJsonGenerator();
		String json = jsonGenerator.toJson(getContent());
		String prettyJson = JsonOutput.prettyPrint(json);
		return prettyJson;
	}

}
