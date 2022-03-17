package com.askimed.nf.test.lang.extensions;

import java.io.BufferedReader;
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
	
	public static String getTextGzip(Path self) throws FileNotFoundException, IOException {

		List<String> lines = new Vector<String>();

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

}
