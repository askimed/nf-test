
package com.askimed.nf.test.core.reports;

import com.askimed.nf.test.core.TestExecutionResult;
import com.askimed.nf.test.core.TestSuiteExecutionResult;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvReportWriter extends AbstractTestReportWriter {

	private String filename;

	public CsvReportWriter(String filename) throws IOException {
		this.filename = filename;
	}

	@Override
	public void writeToFile(List<TestSuiteExecutionResult> testSuites) throws IOException {

		String[] header = new String[]{
				"testsuite",
				"type",
				"test",
				"result",
				"time"
		};

		CSVWriter writer = new CSVWriter(new FileWriter(new File(filename)));
		writer.writeNext(header);

		for (TestSuiteExecutionResult testSuite : testSuites) {

			for (TestExecutionResult test : testSuite.getTests()) {

				String[] line = new String[]{
						testSuite.getTestSuite().getName(),
						testSuite.getTestSuite().getClass().toString(),
						test.getTest().getName(),
						test.getStatus().toString(),
						test.getExecutionTimeInSecs() + ""
				};

				writer.writeNext(line);

			}
		}

		writer.close();

		System.out.println("Wrote csv report to file " + filename + "\n");

	}

}
