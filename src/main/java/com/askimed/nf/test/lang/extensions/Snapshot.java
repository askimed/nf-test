package com.askimed.nf.test.lang.extensions;

import java.util.Date;

import com.askimed.nf.test.core.ITest;

public class Snapshot {

	private Object actual;

	private ITest test;

	private SnapshotFile file;

	public Snapshot(Object actual, ITest test) {
		this.actual = actual;
		this.file = SnapshotFile.loadByTestSuite(test.getTestSuite());
		this.test = test;
	}

	public boolean match() {
		return match(test.getName());
	}

	public boolean match(String id) {
		SnapshotFileItem expected = file.getSnapshot(id);
		if (expected == null) {
			file.updateSnapshot(id, actual);
			file.save();
			return true;
		}

		try {
			return new SnapshotFileItem(new Date(), actual).equals(expected);
		} catch (Exception e) {
			// test failes
			if (test.isUpdateSnapshot()) {
				// udpate snapshot
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
