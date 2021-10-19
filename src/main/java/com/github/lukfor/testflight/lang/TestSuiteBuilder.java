package com.github.lukfor.testflight.lang;

import java.io.File;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.github.lukfor.testflight.core.ITestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyShell;

public class TestSuiteBuilder {

	static WorkflowTestSuite nextflow(
			@DelegatesTo(value = WorkflowTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final WorkflowTestSuite dsl = new WorkflowTestSuite();

		closure.setDelegate(dsl);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return dsl;

	}

	static WorkflowTestSuite process(
			@DelegatesTo(value = WorkflowTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final WorkflowTestSuite dsl = new WorkflowTestSuite();

		closure.setDelegate(dsl);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return dsl;

	}

	
	public static ITestSuite parse(String filename) throws Exception {

		ImportCustomizer customizer = new ImportCustomizer();
		customizer.addStaticImport("com.github.lukfor.testflight.lang.TestSuiteBuilder", "nextflow");
		customizer.addStaticImport("com.github.lukfor.testflight.lang.TestSuiteBuilder", "process");


		CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
		compilerConfiguration.addCompilationCustomizers(customizer);

		GroovyShell shell = new GroovyShell(compilerConfiguration);

		Object object = shell.evaluate(new File(filename));
		ITestSuite nextflowDsl = (ITestSuite) object;

		return nextflowDsl;
	}

}