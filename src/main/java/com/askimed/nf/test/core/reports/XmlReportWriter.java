
package com.askimed.nf.test.core.reports;

import java.io.IOException;
import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.io.FileWriter;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import com.askimed.nf.test.core.TestExecutionResult;
import com.askimed.nf.test.core.TestExecutionResultStatus;
import com.askimed.nf.test.core.TestSuiteExecutionResult;

public class XmlReportWriter extends AbstractTestReportWriter {

	private String filename;

	public XmlReportWriter(String filename) throws IOException {
		this.filename = filename;
	}

	public static String convertTime(long time){
		Date date = new Date(time);
		Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
		return format.format(date);
	}

	@Override
	public void writeToFile(List<TestSuiteExecutionResult> testSuites) throws Exception {
		XMLStreamWriter writer = null;
		try {
			String filePath = this.filename;
			Writer fileWriter = new FileWriter(filePath);
			XMLOutputFactory xmlOutputFactory = XMLOutputFactory.newInstance();

			writer = xmlOutputFactory.createXMLStreamWriter(fileWriter);
			writer.writeStartDocument("utf-8", "1.0");
			writer.writeStartElement("testsuites");
			for (TestSuiteExecutionResult testSuite : testSuites) {
				writer.writeStartElement("testsuite");
				writer.writeAttribute("name", testSuite.getTestSuite().getName());
				writer.writeAttribute("time", Double.toString(testSuite.getExecutionTimeInSecs()));
				writer.writeAttribute("tests", Integer.toString(testSuite.getTests().size()));
				writer.writeAttribute("skipped", Integer.toString(testSuite.getSkipped()));
				writer.writeAttribute("failures", Integer.toString(testSuite.getFailed()));
				writer.writeAttribute("timestamp", convertTime(testSuite.getStartTime()));

				for (TestExecutionResult test : testSuite.getTests()) {
					writer.writeStartElement("testcase");
					writer.writeAttribute("classname", testSuite.getTestSuite().getName());
					writer.writeAttribute("name", test.getTest().getName());
					writer.writeAttribute("time", Double.toString(test.getExecutionTimeInSecs()));
					writer.writeAttribute("status", test.getStatus().toString());

					if (test.getStatus() != TestExecutionResultStatus.PASSED) {
						writer.writeStartElement("failure");
						writer.writeAttribute("message", test.getThrowable().toString());
						writer.writeCharacters(test.getErrorReport());
						writer.writeEndElement();
					}
					writer.writeEndElement();
				}
				writer.writeEndElement();
			}
			writer.writeEndElement();
			writer.writeEndDocument();

			writer.flush();
		}
		finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
}
