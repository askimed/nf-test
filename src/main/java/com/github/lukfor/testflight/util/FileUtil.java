package com.github.lukfor.testflight.util;

import java.io.File;

public class FileUtil {

	public static String path(String... paths) {
		String result = "";
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if (path != null && !path.isEmpty()) {
				if (i > 0 && !path.startsWith(File.separator) && !result.endsWith(File.separator)) {
					if (result.isEmpty()) {
						result += path;
					} else {
						result += File.separator + path;
					}
				} else {
					result += path;
				}
			}
		}
		return result;
	}

	public static boolean createDirectory(String dir) {
		return createDirectory(new File(dir));
	}

	public static boolean createDirectory(File output) {
		if (!output.exists()) {
			return output.mkdirs();
		}
		return true;
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

}
