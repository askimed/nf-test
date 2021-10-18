package com.github.lukfor.testflight.lang;

import java.io.File;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyShell;

public class NextflowTestSuiteBuilder {

	static NextflowTestSuite nextflow(
			@DelegatesTo(value = NextflowTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final NextflowTestSuite dsl = new NextflowTestSuite();

		closure.setDelegate(dsl);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return dsl;

	}

	public static NextflowTestSuite parse(String filename) throws Exception {

		ImportCustomizer customizer = new ImportCustomizer();
		customizer.addStaticImport("com.github.lukfor.testflight.lang.NextflowTestSuiteBuilder", "nextflow");

		CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
		compilerConfiguration.addCompilationCustomizers(customizer);

		GroovyShell shell = new GroovyShell(compilerConfiguration);

		Object object = shell.evaluate(new File(filename));
		NextflowTestSuite nextflowDsl = (NextflowTestSuite) object;

		return nextflowDsl;
	}

}