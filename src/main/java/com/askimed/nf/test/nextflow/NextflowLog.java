package com.askimed.nf.test.nextflow;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.Vector;
import java.util.regex.*;

public class NextflowLog {

	public static String PATTERN = "%d{MMM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n";

	public static String logPatternRegex = "^(?<timestamp>(?<month>Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s\\[(?<thread>.*?)\\]\\s(?<level>\\S+)\\s(?<logger>.+)\\s+-\\s(?<message>.*)$";
	public static Pattern pattern = Pattern.compile(logPatternRegex);

	public static List<String> parseLines(File file, NextflowLogLevel level) throws IOException {
		List<String> lines = Files.readAllLines(file.toPath(), Charset.defaultCharset());

		List<String> parsedLines = new Vector<String>();
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
				if (matcher.group("level").equals(level.toString())) {
					parsedLines.add(matcher.group("message"));
				}
			}
		}

		return parsedLines;
	}

}
