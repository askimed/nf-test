package com.askimed.nf.test.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import groovy.lang.Writable;

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

	static public boolean deleteDirectory(File path) throws IOException {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return false;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (Files.isSymbolicLink(files[i].toPath())) {
						Files.delete(files[i].toPath());
					} else {
						deleteDirectory(files[i]);
					}
				} else {
					Files.delete(files[i].toPath());
				}
			}
			Files.delete(path.toPath());
			return true;
		}
		return false;
	}

	static public void write(File file, Writable writable) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(writable.toString());
		writer.close();
	}

	public static String readFileAsString(File file) throws IOException {

		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();

	}

	public static String makeRelative(File baseDir, File absoluteFile) {
		return baseDir.toURI().relativize(absoluteFile.toURI()).getPath();
	}

	public static String getMd5(Path self) throws IOException, NoSuchAlgorithmException {
		Formatter fm = new Formatter();
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(Files.readAllBytes(self));
		byte[] md5sum = md.digest();
		for (byte b : md5sum) {
			fm.format("%02x", b);
		}
		String result = fm.out().toString();
		fm.close();
		return result;
	}

	public static Path[] list(Path self) {
		File[] files = self.toFile().listFiles();
		Path[] paths = new Path[files.length];
		for (int i = 0; i < files.length; i++) {
			paths[i] = files[i].toPath();
		}
		return paths;
	}

}
