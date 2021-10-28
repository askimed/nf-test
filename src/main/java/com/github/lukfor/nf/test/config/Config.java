package com.github.lukfor.nf.test.config;

import java.io.File;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.github.lukfor.nf.test.lang.pipeline.PipelineTestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyShell;

public class Config {

	public static final String FILENAME = "nf-test.config";
	
	public static final String DEFAULT_NEXTFLOW_CONFIG = "tests/nextflow.config";

	private String testsDir = "tests";

	private String profile = null;

	private String configFile = DEFAULT_NEXTFLOW_CONFIG;

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
		if (profile != null) {
			if (profile.trim().isEmpty()) {
				return null;
			} else {
				return profile;
			}
		} else {
			return null;
		}
	}

	public void configFile(String config) {
		this.configFile = config;
	}

	public File getConfigFile() {
		return new File(configFile);
	}

	static Config config(
			@DelegatesTo(value = PipelineTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final Config config = new Config();

		closure.setDelegate(config);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return config;

	}

	public static Config parse(File script) throws Exception {

		if (!script.exists()) {
			throw new Exception(
					"Error: This pipeline has no valid nf-test config file. Create one with the init command.");
		}

		ImportCustomizer customizer = new ImportCustomizer();
		customizer.addStaticImport("com.github.lukfor.nf.test.config.Config", "config");

		CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
		compilerConfiguration.addCompilationCustomizers(customizer);

		GroovyShell shell = new GroovyShell(compilerConfiguration);

		Object object = shell.evaluate(script);
		Config config = (Config) object;

		return config;
	}

}
