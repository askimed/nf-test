package com.askimed.nf.test.lang;

import java.io.File;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.askimed.nf.test.config.Config;
import com.askimed.nf.test.core.ITestSuite;
import com.askimed.nf.test.lang.function.FunctionTestSuite;
import com.askimed.nf.test.lang.pipeline.PipelineTestSuite;
import com.askimed.nf.test.lang.process.ProcessTestSuite;
import com.askimed.nf.test.lang.workflow.WorkflowTestSuite;
import com.askimed.nf.test.plugins.PluginManager;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyShell;

public class TestSuiteBuilder {

	private static Config config = null;

	public static void setConfig(Config config) {
		TestSuiteBuilder.config = config;
	}

	public static ITestSuite nextflow_pipeline(final Closure closure) {

		PipelineTestSuite suite = new PipelineTestSuite();
		executeClosure(suite, closure);

		return suite;

	}

	public static ITestSuite nextflow_workflow(Closure closure) {

		WorkflowTestSuite suite = new WorkflowTestSuite();
		executeClosure(suite, closure);

		return suite;

	}

	public static ITestSuite nextflow_process(Closure closure) {

		ProcessTestSuite suite = new ProcessTestSuite();
		executeClosure(suite, closure);

		return suite;

	}

	public static ITestSuite nextflow_function(
			@DelegatesTo(value = PipelineTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		FunctionTestSuite suite = new FunctionTestSuite();

		executeClosure(suite, closure);

		return suite;

	}

	private static void executeClosure(ITestSuite suite, Closure closure) {
		if (config != null) {
			suite.configure(config);
		}

		closure.setDelegate(suite);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();
	}

	public static ITestSuite parse(File script) throws Exception {
		return parse(script, "", null);
	}

	public static ITestSuite parse(File script, PluginManager pluginManager) throws Exception {
		return parse(script, "", pluginManager);
	}

	public static ITestSuite parse(File script, String libDir, PluginManager pluginManager) throws Exception {

		ImportCustomizer customizer = new ImportCustomizer();
		customizer.addStaticImport("com.askimed.nf.test.lang.TestSuiteBuilder", "nextflow_pipeline");
		customizer.addStaticImport("com.askimed.nf.test.lang.TestSuiteBuilder", "nextflow_workflow");
		customizer.addStaticImport("com.askimed.nf.test.lang.TestSuiteBuilder", "nextflow_process");
		customizer.addStaticImport("com.askimed.nf.test.lang.TestSuiteBuilder", "nextflow_function");
		customizer.addStaticStars("com.askimed.nf.test.lang.extensions.GlobalMethods");

		ClassLoader classLoader = TestSuiteBuilder.class.getClassLoader();
		if (pluginManager != null) {
			for (String staticImport : pluginManager.getStaticImports()) {
				customizer.addStaticStars(staticImport);
			}
			classLoader = pluginManager.getClassLoader();
		}

		CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
		String classpath = script.getAbsoluteFile().getParentFile().getAbsolutePath() + "/lib:" + libDir;
		compilerConfiguration.setClasspath(classpath);

		compilerConfiguration.addCompilationCustomizers(customizer);

		GroovyShell shell = new GroovyShell(classLoader, compilerConfiguration);

		Object object = shell.evaluate(script);

		if (!(object instanceof ITestSuite)) {
			throw new Exception("Not a valid TestSuite object.");
		}

		ITestSuite testSuite = (ITestSuite) object;
		testSuite.setFilename(script.getAbsolutePath());

		return testSuite;
	}

}