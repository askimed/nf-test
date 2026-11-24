package com.askimed.nf.test.lang.extensions;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;

import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.runtime.powerassert.PowerAssertionError;

import groovy.lang.GroovyShell;

public class GlobalMethodsTest {
    private GroovyShell shell;

    // Setup
	@BeforeEach
	public void setUp() throws IOException {
        // Prepare groovy shell with GlobalMethods class
        ImportCustomizer customizer = new ImportCustomizer();
        customizer.addStaticStars("com.askimed.nf.test.lang.extensions.GlobalMethods");

        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.addCompilationCustomizers(customizer);

        shell = new GroovyShell(compilerConfiguration);
	}


    @Test
    public void testAssertAll() throws Exception {
        // Run groovy script
        Throwable thrown = assertThrows(PowerAssertionError.class, () -> {
            Object object = shell.evaluate(new File("./test-data/assertAll.groovy"));
        });

        // Assert the message
        assertTrue(thrown.toString().contains("3 of 4 assertions failed"));
    }

    // Add test for assertContainsInAnyOrder
    @Test
    public void testAssertInAnyOrderSuccessfulCases() throws Exception {
        // Run groovy script
        shell.evaluate(new File("./test-data/assertInAnyOrderSuccessfulCases.groovy"));
    }

    @Test
    public void testAssertInAnyOrderFailureCases() throws Exception {
        // Run groovy script
        Throwable thrown = assertThrows(PowerAssertionError.class, () -> {
            Object object = shell.evaluate(new File("./test-data/assertInAnyOrderFailureCases.groovy"));
        });
    }

}
