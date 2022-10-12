package com.askimed.nf.test.lang.extensions;

import com.askimed.nf.test.core.ITestSuite;

public class Snapshot {

	private Object actual;

	private ITestSuite suite;

	private SnapshotFile file;

	public Snapshot(Object actual, ITestSuite suite) {
		this.actual = actual;
		this.suite = suite;
		this.file = SnapshotFile.loadByTestSuite(suite);
	}

	public boolean match(String id) {
		SnapshotFileItem expected = file.getSnapshot(id);
		if (expected == null) {
			file.updateSnapshot(id, actual);
			file.save();
			return true;
		} else {
			return new SnapshotFileItem(actual).equals(expected);
		}

	}

}
