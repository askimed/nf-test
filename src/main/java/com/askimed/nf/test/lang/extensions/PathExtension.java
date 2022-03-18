package com.askimed.nf.test.lang.extensions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import groovy.json.JsonSlurper;

public class PathExtension {

	public static String getMd5(Path self) throws IOException, NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(Files.readAllBytes(self));
		byte[] md5sum = md.digest();
		BigInteger bigInt = new BigInteger(1, md5sum);
		return bigInt.toString(16);

	}

	public static List<String> readLinesGzip(Path self) throws FileNotFoundException, IOException {

		List<String> lines = new Vector<String>();

		GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(self.toFile()));
		BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
		String line = null;
		while ((line = br.readLine()) != null) {

			lines.add(line);
		}
		gzip.close();

		return lines;
	}

	public static List<String> getLinesGzip(Path self) throws FileNotFoundException, IOException {

		return readLinesGzip(self);
	}

	public static String getTextGzip(Path self) throws FileNotFoundException, IOException {

		GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(self.toFile()));
		BufferedReader br = new BufferedReader(new InputStreamReader(gzip));
		String line = null;
		StringBuilder text = new StringBuilder();
		int i = 0;
		while ((line = br.readLine()) != null) {
			if (i > 0) {
				text.append("\n");
			}
			text.append(line);
			i++;
		}
		gzip.close();

		return text.toString();
	}

	public static Object readJSON(Path self) throws FileNotFoundException, IOException {

		JsonSlurper jsonSlurper = new JsonSlurper();
		return jsonSlurper.parse(self);

	}

	public static Object getJson(Path self) throws FileNotFoundException, IOException {
		return readJSON(self);
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
