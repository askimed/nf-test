package com.askimed.nf.test.lang.extensions;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.util.*;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import groovy.lang.Closure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Snapshot {

	private Object actual;

	private ITest test;

	private SnapshotFile file;

	private static Logger log = LoggerFactory.getLogger(Snapshot.class);

	public static String ROOT_OBJECT = "contents";

	public Snapshot(Object actual, ITest test) {
		//this.actual = actual;
		this.actual = ObjectUtil.toMap(actual);
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
				log.debug("Snapshots '{}' do not match.", id);
				throw e;
			}
		}

	}

	private DocumentContext createDocumentContext() {
		Map<String, Object> snapshots = new HashMap<String, Object>();
		snapshots.put(ROOT_OBJECT, actual);
		return JsonPath.parse(snapshots);
	}

	public Snapshot replace(Object value) {
		return replace("$..*", value);
	}

	public Snapshot replace(String selector, Object value) {
		DocumentContext json = createDocumentContext();
		json.set(selector, value);
		return this;
	}

	public Snapshot map(Closure closure) {
		return map("$..*", closure);
	}

	public Snapshot map(String selector, Closure closure) {
		DocumentContext json = createDocumentContext();
		json.map(selector, (currentValue, configuration) -> {
			return closure.call(currentValue);
		});
		return this;
	}

	public Snapshot traverse(Closure closure) {

		MapTraverser.traverse(ROOT_OBJECT, actual, new MapOperation() {
			@Override
			public Object map(String path, Object value) {
				return closure.call(path, value);
			}
		});

		return this;
	}

	public Snapshot remove(String selector) {
		DocumentContext json = createDocumentContext();
		json.delete(selector);
		return this;
	}

	public Snapshot view() {
		return view(false);
	}

	public Snapshot view(boolean raw) {
		String json = raw ? ObjectUtil.toJsonRaw(actual) : ObjectUtil.toJson(actual);
		System.out.println();
		System.out.println(AnsiText.padding(AnsiColors.cyan("Snapshot: \n" + json), 6));
		return this;
	}

}
