package com.askimed.nf.test.lang;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.askimed.nf.test.lang.TestSuiteBuilder;

public class NextflowTestSuiteBuilderTest {

	@Test
	public void testParse() throws Exception {
		File file = new File("test-data/test1.nf.test");
		TestSuiteBuilder.parse(file);
	}
	
}
