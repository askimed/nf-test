package com.askimed.nf.test.lang.extensions;

import java.util.Date;

import org.codehaus.groovy.GroovyException;

import groovy.json.JsonGenerator;
import groovy.json.JsonOutput;
import groovyjarjarantlr4.v4.parse.ANTLRParser.throwsSpec_return;

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

		JsonGenerator jsonGenerator = SnapshotFile.createJsonGenerator();
		String json = jsonGenerator.toJson(getContent());
		String prettyJson = JsonOutput.prettyPrint(json);

		String jsonOther = jsonGenerator.toJson(snapshotItem.getContent());
		String prettyJsonOther = JsonOutput.prettyPrint(jsonOther);

		if (prettyJson.equals(prettyJsonOther)) {
			return true;
		}

		throw new RuntimeException("Different Snapshot: \nFound: " + prettyJson + "\n\nExpected:\n" + prettyJsonOther);

	}

}
