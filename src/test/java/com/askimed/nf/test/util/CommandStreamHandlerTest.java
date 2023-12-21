package com.askimed.nf.test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CommandStreamHandlerTest {
    final ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
    final PrintStream stdout = System.out;

    @BeforeEach
    public void captureStandardOut() {
        System.setOut(new PrintStream(capturedOut));
    }

    @Test
    public void testCommandStreamHandler() {
        // output snippet taken from `nextflow run nf-core/rnaseq -revision 3.4`
        String commandOutput =
            "------------------------------------------------------\n"
          + "If you use nf-core/rnaseq for your analysis please cite:\n"
          + "\n"
          + "* The pipeline\n"
          + "  https://doi.org/10.5281/zenodo.1400710\n"
          + "\n"
          + "* The nf-core framework\n"
          + "  https://doi.org/10.1038/s41587-020-0439-x\n"
          + "\n"
          + "* Software dependencies\n"
          + "  https://github.com/nf-core/rnaseq/blob/master/CITATIONS.md\n"
          + "------------------------------------------------------\n";

        ByteArrayInputStream is = new ByteArrayInputStream(commandOutput.getBytes());
        CommandStreamHandler handler = new CommandStreamHandler(is);
        handler.run();

        String expectedOutput =
            "    > ------------------------------------------------------\n"
          + "    > If you use nf-core/rnaseq for your analysis please cite:\n"
          + "    > \n"
          + "    > * The pipeline\n"
          + "    >   https://doi.org/10.5281/zenodo.1400710\n"
          + "    > \n"
          + "    > * The nf-core framework\n"
          + "    >   https://doi.org/10.1038/s41587-020-0439-x\n"
          + "    > \n"
          + "    > * Software dependencies\n"
          + "    >   https://github.com/nf-core/rnaseq/blob/master/CITATIONS.md\n"
          + "    > ------------------------------------------------------\n";

        assertEquals(
            expectedOutput,
            capturedOut.toString()
        );
    }

    @AfterEach
    public void resetStandardOut() {
        System.setOut(stdout);
        capturedOut.reset();
    }
}
