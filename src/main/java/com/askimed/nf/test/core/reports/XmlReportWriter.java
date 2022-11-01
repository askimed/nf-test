
package com.askimed.nf.test.core.reports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


import org.tap4j.model.Plan;
import org.tap4j.model.TestResult;
import org.tap4j.model.TestSet;
import org.tap4j.producer.TapProducer;
import org.tap4j.producer.TapProducerFactory;
import org.tap4j.util.StatusValues;

import com.askimed.nf.test.core.TestExecutionResult;
import com.askimed.nf.test.core.TestExecutionResultStatus;
import com.askimed.nf.test.core.TestSuiteExecutionResult;

public class XmlReportWriter extends AbstractTestReportWriter {

	private String filename;

	public XmlReportWriter(String filename) throws IOException {
		this.filename = filename;
	}

	public void print_message() {
		System.out.println("I am a XmlReportWriter in print_message");
		System.out.println(this.filename);
	}

	@Override
	public void writeToFile(List<TestSuiteExecutionResult> testSuites) {
		System.out.println("TODO: Write xml to file..." + this.filename);
		// System.out.println("TODO: Write xml to file...");
		return;

		// int index = 0;

		// TapProducer tapProducer = TapProducerFactory.makeTap13YamlProducer();
		// TestSet testSet = new TestSet();
		// Plan plan = new Plan(getCount());
		// testSet.setPlan(plan);

		// for (TestSuiteExecutionResult testSuite : testSuites) {

		// 	for (TestExecutionResult test : testSuite.getTests()) {
		// 		index++;
		// 		// TODO: what is the best way to handle testsuites? subtests?
		// 		String name = testSuite.getTestSuite().getName() + ": " + test.getTest().getName();
		// 		StatusValues status = toStatusValue(test.getStatus());

		// 		TestResult tapResult = new TestResult(status, index);
		// 		tapResult.setDescription(name);
		// 		if (test.getStatus() != TestExecutionResultStatus.PASSED) {
		// 			Map<String, Object> map = new HashMap<String, Object>();
		// 			map.put("failure", test.getThrowable().toString());
		// 			map.put("output", test.getErrorReport());
		// 			tapResult.setDiagnostic(map);
		// 		}
		// 		testSet.addTestResult(tapResult);
		// 	}
		// }

		// tapProducer.dump(testSet, new File(filename));

		// System.out.println("Wrote TAP report to file " + filename + "\n");

	}

	protected StatusValues toStatusValue(TestExecutionResultStatus status) {
		return (status == TestExecutionResultStatus.PASSED) ? StatusValues.OK : StatusValues.NOT_OK;
	}

}
