package com.github.lukfor.testflight.lang;

import java.io.File;

import org.junit.jupiter.api.Test;

public class NextflowTestSuiteBuilderTest {

	@Test
	public void testParse() throws Exception {
		File file = new File("test-data/test1.nf.test");
		TestSuiteBuilder.parse(file);
	}
	
}
