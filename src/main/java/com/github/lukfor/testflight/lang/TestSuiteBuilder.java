package com.github.lukfor.testflight.lang;

import java.io.File;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.github.lukfor.testflight.core.ITestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyShell;

public class TestSuiteBuilder {

	static ITestSuite nextflow(
			@DelegatesTo(value = WorkflowTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final WorkflowTestSuite suite = new WorkflowTestSuite();

		closure.setDelegate(suite);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return suite;

	}

	static ITestSuite nextflow_process(
			@DelegatesTo(value = WorkflowTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final ProcessTestSuite suite = new ProcessTestSuite();

		closure.setDelegate(suite);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return suite;

	}

	public static ITestSuite parse(String filename) throws Exception {

		ImportCustomizer customizer = new ImportCustomizer();
		customizer.addStaticImport("com.github.lukfor.testflight.lang.TestSuiteBuilder", "nextflow");
		customizer.addStaticImport("com.github.lukfor.testflight.lang.TestSuiteBuilder", "nextflow_process");

		CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
		compilerConfiguration.addCompilationCustomizers(customizer);

		GroovyShell shell = new GroovyShell(compilerConfiguration);

		Object object = shell.evaluate(new File(filename));
		ITestSuite nextflowDsl = (ITestSuite) object;

		return nextflowDsl;
	}

}