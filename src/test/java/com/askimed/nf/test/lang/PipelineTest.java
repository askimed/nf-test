package com.askimed.nf.test.lang;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.App;
import com.askimed.nf.test.util.FileUtil;

public class PipelineTest {

	@BeforeAll
	public static void setUp() throws IOException {
		FileUtil.deleteDirectory(new File(".nf-test"));
		new File("nf-test.config").delete();
	}

	@Test
	public void testDsl1() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/pipeline/dsl1/test1.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testMultipleDsl1Scripts() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/pipeline/dsl1/test2.nf.test",
				"test-data/pipeline/dsl1/test1.nf.test" });
		assertEquals(1, exitCode);
	}

	@Test
	public void testMultipleDsl1ScriptsAndWriteTapOutput() throws Exception {

		String tapOutput = "output.tap";

		File output = new File(tapOutput);
		if (output.exists()) {
			output.delete();
		}
		assertFalse(output.exists());

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/pipeline/dsl1/test2.nf.test",
				"test-data/pipeline/dsl1/test1.nf.test", "--tap", tapOutput });
		assertEquals(1, exitCode);
		assertTrue(output.exists());
	}

	@Test
	public void testMultipleDsl1ScriptsAndWriteXmlOutput() throws Exception {

		String xmlOutput = "output.junit.xml";

		File output = new File(xmlOutput);
		if (output.exists()) {
			output.delete();
		}
		assertFalse(output.exists());

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/pipeline/dsl1/test2.nf.test",
				"test-data/pipeline/dsl1/test1.nf.test", "--junitxml", xmlOutput });
		assertEquals(1, exitCode);
		assertTrue(output.exists());
		assertXmlSchemaValidation("test-data/pipeline/junit_schema.xsd", xmlOutput);
	}

	@Test
	public void testDsl2() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/pipeline/dsl2/trial.nf.test" });
		assertEquals(0, exitCode);

	}

	@Test
	public void testPipelineThatUsesProcessKeywordShouldFail() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/pipeline/dsl2/trial.process-keyword.nf.test" });
		assertEquals(1, exitCode);

	}
	
	@Test
	public void testPipelineThatUsesWorkflowKeywordShouldFail() throws Exception {

		App app = new App();
		int exitCode = app.run(new String[] { "test", "test-data/pipeline/dsl2/trial.workflow-keyword.nf.test" });
		assertEquals(1, exitCode);

	}
	
	// Thanks: https://www.digitalocean.com/community/tutorials/how-to-validate-xml-against-xsd-in-java
	public static void assertXmlSchemaValidation(String xsdPath, String xmlPath) throws Exception{
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = factory.newSchema(new File(xsdPath));
		Validator validator = schema.newValidator();
		validator.validate(new StreamSource(new File(xmlPath)));
	}
}
