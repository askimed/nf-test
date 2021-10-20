package com.github.lukfor.testflight.util;

import java.io.File;
import java.nio.file.Path;

public class FileAndPathMethods {

	public static File file(String filename) {
		return new File(filename);
	}

	public static Path path(String filename) {
		return Path.of(filename);
	}
}
