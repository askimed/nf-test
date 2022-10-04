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

		Map<String, String> fasta = new HashMap<String, String>();
		
		String sample = null;

		BufferedReader br = new BufferedReader(new InputStreamReader(openTxtOrGzipStream(path)));
		String line = null;
		while ((line = br.readLine()) != null) {
			if (line.trim().startsWith(">")) {
				sample = line.substring(1).trim();
				fasta.put(sample, "");
			} else {
				if (sample == null ) {
					throw new IOException("Fasta file is malformed. Starts with sequence.");
				}
				String sequence = fasta.get(sample);
				fasta.put(sample, sequence + line.trim());
			}
		}
		br.close();
		
		return fasta;
	}
	
	
	private static DataInputStream openTxtOrGzipStream(Path path) throws IOException {
		FileInputStream inputStream = new FileInputStream(path.toFile());
		InputStream in2 = FileUtil.decompressStream(inputStream);
		return new DataInputStream(in2);
	}


}
