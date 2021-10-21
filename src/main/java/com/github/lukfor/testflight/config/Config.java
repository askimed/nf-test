package com.github.lukfor.testflight.config;

import java.io.File;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.github.lukfor.testflight.lang.workflow.WorkflowTestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyShell;

public class Config {

	public static final String FILENAME = "nf-flighttest.config";

	private String testsDir = "tests";

	private String profile = null;

	public void testsDir(String testsDir) {
		this.testsDir = testsDir;
	}

	public String getTestsDir() {
		return testsDir;
	}

	public void profile(String profile) {
		this.profile = profile;
	}

	public String getProfile() {
		return profile;
	}

	static Config config(
			@DelegatesTo(value = WorkflowTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final Config config = new Config();

		closure.setDelegate(config);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return config;

	}

	public static Config parse(File script) throws Exception {

		if (!script.exists()) {
			throw new Exception("Error: This pipeline has no valid nf-flightest config file. Create one with the init command.");
		}

		ImportCustomizer customizer = new ImportCustomizer();
		customizer.addStaticImport("com.github.lukfor.testflight.config.Config", "config");

		CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
		compilerConfiguration.addCompilationCustomizers(customizer);

		GroovyShell shell = new GroovyShell(compilerConfiguration);

		Object object = shell.evaluate(script);
		Config config = (Config) object;

		return config;
	}

}
