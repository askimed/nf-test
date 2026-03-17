package com.askimed.nf.test.nextflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.HashMap;

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

	@Test
    void resumeFlagIsInjectedWhenEnabled() throws Exception {

        NextflowCommand command = new NextflowCommand();

        command.setScript("main.nf");
        command.setParams(new HashMap<>());
        command.setResume(true);

        List<String> args = command.buildArgs();

        assertTrue(args.contains("-resume"),
                "Expected -resume to be present in arguments when resume=true");
    }

	@Test
    void resumeFlagIsNotInjectedWhenDisabled() throws Exception {

        NextflowCommand command = new NextflowCommand();

        command.setScript("main.nf");
        command.setParams(new HashMap<>());
        command.setResume(false);

        List<String> args = command.buildArgs();

        assertFalse(args.contains("-resume"),
                "Did not expect -resume when resume=false");
    }

    @Test
    void getVersion() {
		assertNotEquals("unknown", NextflowCommand.getVersion());
    }
}
