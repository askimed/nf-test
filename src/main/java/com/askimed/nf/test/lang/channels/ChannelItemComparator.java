package com.askimed.nf.test.lang.channels;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

import com.askimed.nf.test.lang.extensions.PathExtension;
import com.askimed.nf.test.util.AnsiColors;

public class ChannelItemComparator implements Comparator<Object> {

	@Override
	@SuppressWarnings("unchecked")
	public int compare(Object o1, Object o2) {

		if (o1 instanceof ArrayList) {
			ArrayList<Object> tuple1 = (ArrayList<Object>) o1;
			ArrayList<Object> tuple2 = (ArrayList<Object>) o2;
			for (int i = 0; i < tuple1.size(); i++) {
				Object a = tuple1.get(i);
				Object b = tuple2.get(i);

				int result = compareObjects(a, b);

				if (result != 0) {
					return result;
				}

			}
		} else {
			return compareObjects(o1, o2);
		}

		return 0;
	}

	@SuppressWarnings("rawtypes")
	public int compareObjects(Object a, Object b) {

		if (a.getClass() != b.getClass()) {
			System.err.println(AnsiColors
					.yellow("\nWarning: Cannot sort channel, order not deterministic. Objects are different types: "
							+ a.getClass() + " vs. " + b.getClass()));
			return 1;
		}

		if (a instanceof String) {
			if (isPath(a)) {
				return comparePaths(a.toString(), b.toString());
			} else {
				return compareStrings(a.toString(), b.toString());
			}
		} else if (isNumber(a)) {
			return compareNumbers((Comparable) a, (Comparable) b);
		} else if (a instanceof Map) {
			return compareMaps((Map) a, (Map) b);
		} else {
			System.err.println(AnsiColors
					.yellow("\nWarning: Cannot sort channel, order not deterministic. Unsupported objects types: "
							+ a.getClass() + " vs. " + b.getClass()));
			return 1;

		}

	}

	private boolean isNumber(Object a) {
		return a instanceof Integer || a instanceof Double || a instanceof Float;
	}

	private boolean isPath(Object a) {
		return a.toString().startsWith("/");
	}

	@SuppressWarnings({ "rawtypes" })
	private int compareMaps(Map a, Map b) {
		// since we converted all nested maps to treemaps, toString returns keys sorted
		// in deterministic order.
		return compareStrings(a.toString(), b.toString());
	}

	public int comparePaths(String a, String b) {
		// sort path by filename
		Path path1 = Path.of(a);
		Path path2 = Path.of(b);
		String name1 = path1.getFileName().toString();
		String name2 = path2.getFileName().toString();
		int result = name1.compareTo(name2);
		if (result != 0) {
			return result;
		}
		// filenames are equal -> sort by hash to get deterministic order
		try {
			String hash1 = PathExtension.getMd5(path1);
			String hash2 = PathExtension.getMd5(path2);
			return hash1.compareTo(hash2);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public int compareStrings(String a, String b) {
		return a.compareTo(b);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int compareNumbers(Comparable a, Comparable b) {
		return a.compareTo(b);
	}

}
