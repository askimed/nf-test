package com.askimed.nf.test.lang.extensions.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

public class GzipUtil {

	public static List<String> readLines(Path path) throws FileNotFoundException, IOException {

		GZIPInputStream gzip = null;

		try {

			gzip = new GZIPInputStream(new FileInputStream(path.toFile()));
			BufferedReader br = new BufferedReader(new InputStreamReader(gzip));

			List<String> lines = new Vector<String>();
			String line = null;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
			return lines;

		} finally {
			gzip.close();
		}

	}

	public static String readText(Path path) throws FileNotFoundException, IOException {

		GZIPInputStream gzip = null;

		try {

			gzip = new GZIPInputStream(new FileInputStream(path.toFile()));
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
			return text.toString();

		} finally {
			gzip.close();
		}

	}

}
