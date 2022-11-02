
package com.askimed.nf.test.core.reports;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
	public void writeToFile(List<TestSuiteExecutionResult> testSuites){
		System.out.println("TODO: Write xml to file..." + this.filename);
        // Try block to check for exceptions
		XMLStreamWriter xmlStreamWriter = null;

		try {
  
            // File Path
            String filePath = this.filename;
  
            // Creating FileWriter object
            Writer fileWriter = new FileWriter(filePath);
  
            // Getting the XMLOutputFactory instance
            XMLOutputFactory xmlOutputFactory
                = XMLOutputFactory.newInstance();
  
            // Creating XMLStreamWriter object from
            // xmlOutputFactory.
            xmlStreamWriter = xmlOutputFactory.createXMLStreamWriter(fileWriter);
  
            // Addoing elements to xmlStreamWriter
            // Custom input element addition
			xmlStreamWriter.writeStartElement("testsuites");
			// int index = 0;
			for (TestSuiteExecutionResult testSuite : testSuites) {
				xmlStreamWriter.writeStartElement("testsuite");
				xmlStreamWriter.writeAttribute("name", testSuite.getTestSuite().getName());
				
				// for (TestExecutionResult test : testSuite.getTests()) {
				// 	index++;
				// 	String name = testSuite.getTestSuite().getName() + ": " + test.getTest().getName();
	
				// 	test.getStatus();
				// 	TestResult tapResult = new TestResult(status, index);
				// 	tapResult.setDescription(name);
				// 	if (test.getStatus() != TestExecutionResultStatus.PASSED) {
				// 		Map<String, Object> map = new HashMap<String, Object>();
				// 		map.put("failure", test.getThrowable().toString());
				// 		map.put("output", test.getErrorReport());
				// 		tapResult.setDiagnostic(map);
				// 	}
				// 	testSet.addTestResult(tapResult);
				// }
				xmlStreamWriter.writeEndElement();
			}
            xmlStreamWriter.writeEndElement();


			// xmlStreamWriter.writeAttribute("id", "10");
            // xmlStreamWriter.writeCharacters("hello world!");
            // xmlStreamWriter.writeCData("more text data");
            // xmlStreamWriter.writeEndElement();
            xmlStreamWriter.writeEmptyElement("used & new");
            xmlStreamWriter.writeComment("Thank you!");
            xmlStreamWriter.writeEndDocument();
  
            // Writing the content on XML file and
            // close xmlStreamWriter using close() method
            xmlStreamWriter.flush();
			xmlStreamWriter.close();
  
            // Display message for successful execution of
            // program
            System.out.println(
                "XML file created successfully.");
        }
		catch (Exception e) {
  
            // Print the line number where exception occurs
            e.printStackTrace();
        }
	}
}
