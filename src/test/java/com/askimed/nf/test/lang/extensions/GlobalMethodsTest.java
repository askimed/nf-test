package com.askimed.nf.test.lang.extensions;

import java.io.File;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.Assert.*;

import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.CompilerConfiguration;

import groovy.lang.GroovyShell;

public class GlobalMethodsTest {

    @Test
    public void testAssertAll() throws Exception {
        // Prepare groovy shell with GlobalMethods class
        ImportCustomizer customizer = new ImportCustomizer();
        customizer.addStaticStars("com.askimed.nf.test.lang.extensions.GlobalMethods");

        CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.addCompilationCustomizers(customizer);

        GroovyShell shell = new GroovyShell(compilerConfiguration);

        // Run groovy script
        Exception exception = assertThrows(Exception.class, () -> {
            Object object = shell.evaluate(new File("./test-data/assertAll.groovy"));
        });

        // Assert the message
        assertTrue(exception.toString().contains("3 of 4 assertions failed"));
    }
}