package com.askimed.nf.test.nextflow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class NextflowCommandTest {

	@Test
	public void testParseOptions() {

		List<String> options = NextflowCommand.parseOptions("--a --b");
		assertEquals(2, options.size());

		options = NextflowCommand.parseOptions("--a \"value with space\" --b");
		assertEquals(3, options.size());
		assertEquals("--a", options.get(0));
		assertEquals("value with space", options.get(1));
		assertEquals("--b", options.get(2));
	}

}
