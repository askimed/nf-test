package com.askimed.nf.test.lang.extensions;

import java.util.Date;

import groovy.json.JsonGenerator;
import groovy.json.JsonOutput;

public class SnapshotFileItem {

	private Object content;

	private Date timestamp;

	public SnapshotFileItem(Date timestamp, Object object) {
		content = object;
		this.timestamp = timestamp;
	}

	public Object getContent() {
		return content;
	}

	public Date getTimestamp() {
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

	@Override
	public String toString() {
		JsonGenerator jsonGenerator = SnapshotFile.createJsonGenerator();
		String json = jsonGenerator.toJson(getContent());
		String prettyJson = JsonOutput.prettyPrint(json);
		return prettyJson;
	}

}
