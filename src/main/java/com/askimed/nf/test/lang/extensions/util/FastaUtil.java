package com.askimed.nf.test.lang.extensions.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.askimed.nf.test.util.FileUtil;

public class FastaUtil {

	public static Map<String, String> readAsMap(Path path) throws IOException {

		BufferedReader br = null;
		try {

			br = new BufferedReader(new InputStreamReader(openTxtOrGzipStream(path)));
			return parse(br);

		} finally {
			br.close();
		}

	}

	private static Map<String, String> parse(BufferedReader br) throws IOException {

		Map<String, String> fasta = new HashMap<String, String>();
		String sample = null;
		String line = null;
		while ((line = br.readLine()) != null) {

			String trimmedLine = line.trim();

			// ignore comments
			if (trimmedLine.isEmpty() || trimmedLine.startsWith(";")) {
				continue;
			}

			if (trimmedLine.startsWith(">")) {
				sample = trimmedLine.substring(1).trim();
				if (fasta.containsKey(sample)) {
					throw new IOException("Duplicate sample " + sample + " detected.");
				}
				fasta.put(sample, "");
			} else {
				if (sample == null) {
					throw new IOException("Fasta file is malformed. Starts with sequence.");
				}
				String sequence = fasta.get(sample);
				fasta.put(sample, sequence + trimmedLine);
			}
		}

		return fasta;

	}

	private static DataInputStream openTxtOrGzipStream(Path path) throws IOException {
		FileInputStream inputStream = new FileInputStream(path.toFile());
		InputStream in2 = FileUtil.decompressStream(inputStream);
		return new DataInputStream(in2);
	}

}
