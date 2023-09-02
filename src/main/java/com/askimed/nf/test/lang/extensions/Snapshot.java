package com.askimed.nf.test.lang.extensions;

import java.io.IOException;

import com.askimed.nf.test.core.ITest;

public class Snapshot {

	private Object actual;

	private ITest test;

	private SnapshotFile file;

	public Snapshot(Object actual, ITest test) {
		this.actual = actual;
		this.file = test.getTestSuite().getSnapshot();
		this.test = test;
	}

	public boolean match() throws IOException {
		return match(test.getName());
	}

	public boolean match(String id) throws IOException {
		SnapshotFileItem expected = file.getSnapshot(id);
		//new snapshot --> create snapshot
		if (expected == null) {
			file.createSnapshot(id, actual);
			file.save();
			return true;
		}

		try {
			//compare actual snapshot with expected
			return new SnapshotFileItem(actual).equals(expected);
		} catch (Exception e) {
			// test failes and flag set --> update snapshot
			if (test.isUpdateSnapshot()) {
				file.updateSnapshot(id, actual);
				file.save();
				return true;
			} else {
				throw e;
			}
		}

	}

	public void view() {

	}

}
