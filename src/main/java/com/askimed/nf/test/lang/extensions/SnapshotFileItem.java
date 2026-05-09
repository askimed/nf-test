package com.askimed.nf.test.lang.extensions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.askimed.nf.test.App;
import com.askimed.nf.test.lang.extensions.util.SnapshotDiffUtil;

import com.askimed.nf.test.nextflow.NextflowCommand;
import com.askimed.nf.test.util.ObjectUtil;

public class SnapshotFileItem {

	private Object content;

	private String timestamp;

	private Map<String, Object> meta = new HashMap<>();

	public static DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

	public SnapshotFileItem(Object content) {
		this(SnapshotFileItem.createTimestamp(), content);
	}

	public SnapshotFileItem(String timestamp, Object content) {
		this.timestamp = timestamp;
		this.content = content;
		this.meta.put(App.NAME, App.VERSION);
		this.meta.put("nextflow", NextflowCommand.getVersion());
	}

	public Object getContent() {
		return content;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public Map<String, Object> getMeta() {
		return meta;
	}

	public void setMeta(Map<String, Object> meta) {
		this.meta = meta;
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
				"Different Snapshot:\n" + SnapshotDiffUtil.getDiff(snapshotItem, this));

	}

	public static String createTimestamp() {
		return DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now());
	}

	@Override
	public String toString() {
		return ObjectUtil.toJson(getContent());
	}

}
