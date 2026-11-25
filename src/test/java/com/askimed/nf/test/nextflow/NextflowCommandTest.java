package com.askimed.nf.test.nextflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Collections;
import java.util.HashMap;
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

	private List<String> buildArgs(boolean resume, String options) throws Exception {
		NextflowCommand command = new NextflowCommand();
		command.setScript("main.nf");
		command.setParams(new HashMap<>());
		command.setOptions(options);
		command.setResume(resume);
		return command.buildArgs();
	}

	@Test
	void resumeFlagIsInjectedWhenEnabled() throws Exception {
		assertEquals(1, Collections.frequency(buildArgs(true, ""), "-resume"));
	}

	@Test
	void resumeFlagIsNotInjectedWhenDisabled() throws Exception {
		assertFalse(buildArgs(false, "").contains("-resume"));
	}

	@Test
	void resumeFlagIsNotDuplicatedWhenInOptions() throws Exception {
		assertEquals(1, Collections.frequency(buildArgs(true, "-resume"), "-resume"));
	}

	@Test
	void doubleDashResumeParamDoesNotEnableResume() throws Exception {
		List<String> args = buildArgs(false, "--resume true");
		assertFalse(args.contains("-resume"));
		assertTrue(args.contains("--resume"));
	}

    @Test
    void getVersion() {
		assertNotEquals("unknown", NextflowCommand.getVersion());
    }
}
