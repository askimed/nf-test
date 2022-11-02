package com.askimed.nf.test.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

public class NextflowTestSuiteBuilderTest {

	@Test
	public void testParse() throws Exception {
		File file = new File("test-data/pipeline/dsl1/test1.nf.test");
		assertEquals(5, TestSuiteBuilder.parse(file).getTests().size());
	}
	
}
