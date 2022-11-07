package com.askimed.nf.test.lang.extensions;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import org.junit.jupiter.api.Test;

import com.askimed.nf.test.lang.extensions.GlobalMethods;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.CompilerConfiguration;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.junit.matchers.JUnitMatchers.*;
import static org.junit.Assert.*;

import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyShell;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.Binding;

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
        assertThat(exception.toString(), containsString("3 of 4 assertions failed"));
	}
}