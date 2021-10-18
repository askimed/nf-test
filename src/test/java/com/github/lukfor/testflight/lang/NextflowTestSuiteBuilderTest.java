package com.github.lukfor.testflight.lang;

import org.junit.jupiter.api.Test;

public class NextflowTestSuiteBuilderTest {

	@Test
	public void testParse() throws Exception {
		NextflowTestSuiteBuilder.parse("test-data/test1.nf.test");
	}
	
}
