package com.askimed.nf.test.lang.extensions.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.askimed.nf.test.lang.extensions.SnapshotFile;
import com.askimed.nf.test.lang.extensions.SnapshotFileItem;
import com.askimed.nf.test.util.BinaryFinder;
import com.askimed.nf.test.util.Command;
import com.askimed.nf.test.util.FileUtil;

import groovy.json.JsonGenerator;
import groovy.json.JsonOutput;

public class SnapshotDiffUtil {

	public static String ENV_DIFF = "NFT_DIFF";
	
	public static String ENV_DIFF_ARGS = "NFT_DIFF_ARGS";

	public static String DEFAULT_DIFF = "diff";

	public static String DEFAULT_DIFF_ARGS = "-y -W 200";

	public static void printDiff(SnapshotFileItem expected, SnapshotFileItem found) {
		System.out.println(getDiff(expected, found));
	}

	public static String getDiff(SnapshotFileItem expected, SnapshotFileItem found) {

		try {
			File expectedFile = File.createTempFile("expected", "json");
			writeSnapshotToFile(expectedFile, expected);
			File foundFile = File.createTempFile("found", "json");
			writeSnapshotToFile(foundFile, found);

			File output = File.createTempFile("output", "diff");

			// use command from ENV_DIFF, otherwise DEFAULT_DIFF
			String binary = System.getenv(ENV_DIFF);
			if (binary == null || binary.trim().isEmpty()) {
				binary = DEFAULT_DIFF;
			}

			// is not a location try to find it in $PATH
			if (!new File(binary).exists()) {
				binary = new BinaryFinder(binary).envPath().path("/usr/local/bin").find();
			}

			// binary not found, return default output
			if (binary == null) {
				return getSimpleOutput(expected, found);
			}
			
			String binaryArgs = System.getenv(ENV_DIFF_ARGS);
			if (binaryArgs == null) {
				binaryArgs = DEFAULT_DIFF_ARGS;
			}


			List<String> args = new Vector<String>();
			Collections.addAll(args, binaryArgs.split(" "));
			args.add(expectedFile.getAbsolutePath());
			args.add(foundFile.getAbsolutePath());

			Command command = new Command(binary);
			command.setSilent(true);
			command.setParams(args);
			command.saveStdOut(output.getAbsolutePath());
			command.execute();
			
			expectedFile.delete();
			foundFile.delete();

			String diff = FileUtil.readFileAsString(output);
			output.delete();

			return diff;

		} catch (IOException e) {
			e.printStackTrace();
			return getSimpleOutput(expected, found);
		}

	}

	public static String getSimpleOutput(SnapshotFileItem expected, SnapshotFileItem found) {
		return "Found:\n" + found.toString() + "\n\nExpected:\n" + expected.toString();
	}

	private static void writeSnapshotToFile(File file, SnapshotFileItem snapshot) throws IOException {
		JsonGenerator jsonGenerator = SnapshotFile.createJsonGenerator();
		String json = jsonGenerator.toJson(snapshot.getContent());
		String prettyJson = JsonOutput.prettyPrint(json);
		FileWriter fileWriter = new FileWriter(file);
		fileWriter.append(prettyJson);
		fileWriter.close();
	}

}
