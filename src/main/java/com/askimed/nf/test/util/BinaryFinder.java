package com.askimed.nf.test.util;

import java.io.File;

public class BinaryFinder {

	private String location = null;

	private String name;

	public BinaryFinder(String name) {

		this.name = name;

	}

	public BinaryFinder env(String variable) {

		if (location != null) {
			return this;
		}
		String path = System.getenv(variable);
		if (path != null && !path.isEmpty()) {
			String binary = FileUtil.path(path, name);
			if (new File(binary).exists()) {
				location = binary;
			}
		}

		return this;

	}

	public BinaryFinder path(String path) {

		if (location != null) {
			return this;
		}

		String binary = FileUtil.path(path, name);
		if (new File(binary).exists()) {
			location = binary;
		}

		return this;
	}

	public BinaryFinder envPath() {
		if (location != null) {
			return this;
		}

		String envPath = System.getenv("PATH");
		if (envPath != null && !envPath.isEmpty()) {
			String[] paths = envPath.split(":");
			for (String path : paths) {
				String binary = FileUtil.path(path, name);
				if (new File(binary).exists()) {
					location = binary;
					return this;
				}
			}
		}

		return this;
	}

	public String find() {
		return location;
	}

}
