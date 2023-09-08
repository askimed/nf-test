package com.askimed.nf.test.lang.extensions;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.askimed.nf.test.core.ITest;

public class Snapshot {

	private Object actual;

	private ITest test;

	private SnapshotFile file;

	private static Logger log = LoggerFactory.getLogger(Snapshot.class);

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
		// new snapshot --> create snapshot
		if (expected == null) {
			log.debug("Snapshot '{}' not found.", id);
			file.createSnapshot(id, actual);
			file.save();
			return true;
		}

		try {
			// compare actual snapshot with expected
			boolean match = new SnapshotFileItem(actual).equals(expected);
			log.debug("Snapshots '{}' match.", id);
			return match;
		} catch (Exception e) {
			// test fails and flag set --> update snapshot
			if (test.isUpdateSnapshot()) {
				log.debug("Snapshots '{}' do not match. Update snapshots flag set.", id);
				file.updateSnapshot(id, actual);
				file.save();
				return true;
			} else {
				log.debug("Snapshots '{}' do not match.", id);
				throw e;
			}
		}

	}

	public void view() {

	}

}
