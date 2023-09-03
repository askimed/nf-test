package com.askimed.nf.test.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;

import org.junit.jupiter.api.Test;

public class NextflowTestSuiteBuilderTest {

	@Test
	public void testParse() throws Throwable {
		File file = new File("test-data/workflow/libs/hello.nf.test");
		assertEquals(4, TestSuiteBuilder.parse(file).getTests().size());
	}
	
}
