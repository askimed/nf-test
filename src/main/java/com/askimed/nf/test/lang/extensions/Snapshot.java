package com.askimed.nf.test.lang.extensions;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.util.AnsiColors;
import com.askimed.nf.test.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

	public Snapshot md5() {
		actual = ObjectUtil.getMd5(actual);
		return this;
	}

	public boolean match(String id) throws IOException {

		//check if match with this id was already called. --> duplicate snapshots.
		if (file.getActiveSnapshots().contains(id)) {
			throw new RuntimeException("A snapshot with id '" + id + "' already exists. " +
					"Snapshot ids have to be unique, and a test can only have one unnamed snapshot.");
		}

		SnapshotFileItem expected = file.getSnapshot(id);
		// new snapshot --> create snapshot
		if (expected == null) {
			if (test.isCIMode()) {
				throw new RuntimeException("CI mode activated and snapshot with id '" + id + "' not found.");
			}
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
				log.error("Snapshots '{}' do not match.", id);
				System.out.println(AnsiColors.red("Error: Snapshots '" + id + "' do not match."));
				throw e;
			}
		}

	}

	public void view() {

	}

}
