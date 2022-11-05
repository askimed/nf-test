package com.askimed.nf.test.lang.extensions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import com.askimed.nf.test.lang.extensions.util.GzipUtil;
import com.askimed.nf.test.util.FileUtil;

import groovy.json.JsonSlurper;
import groovy.yaml.YamlSlurper;


public class PathExtension {

	public static String getMd5(Path self) throws IOException, NoSuchAlgorithmException {
		return FileUtil.getMd5(self);
	}

	/* Gzip support */

	public static List<String> readLinesGzip(Path self) throws FileNotFoundException, IOException {
		return GzipUtil.readLines(self);
	}

	public static List<String> getLinesGzip(Path self) throws FileNotFoundException, IOException {
		return GzipUtil.readLines(self);
	}

	public static String getTextGzip(Path self) throws FileNotFoundException, IOException {
		return GzipUtil.readText(self);
	}

	/* JSON */

	public static Object readJSON(Path self) throws FileNotFoundException, IOException {
		JsonSlurper jsonSlurper = new JsonSlurper();
		return jsonSlurper.parse(self);
	}

	public static Object getJson(Path self) throws FileNotFoundException, IOException {
		return readJSON(self);
	}

	/* YAML */

	public static Object getYaml(Path self) throws FileNotFoundException, IOException {
		YamlSlurper yamlSlurper = new YamlSlurper();
		return yamlSlurper.parse(self);
	}

	/* File methods */

	public static Path[] list(Path self) {

		File[] files = self.toFile().listFiles();
		Arrays.sort(files);
		Path[] paths = new Path[files.length];
		for (int i = 0; i < files.length; i++) {
			paths[i] = files[i].toPath();
		}
		return paths;

	}

	public static boolean exists(Path self) {
		return Files.exists(self);
	}

}
