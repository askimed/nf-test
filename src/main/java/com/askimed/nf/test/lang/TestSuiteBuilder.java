package com.askimed.nf.test.lang;

import java.io.File;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.customizers.ImportCustomizer;

import com.askimed.nf.test.core.ITestSuite;
import com.askimed.nf.test.lang.extensions.PluginManager;
import com.askimed.nf.test.lang.function.FunctionTestSuite;
import com.askimed.nf.test.lang.pipeline.PipelineTestSuite;
import com.askimed.nf.test.lang.process.ProcessTestSuite;
import com.askimed.nf.test.lang.workflow.WorkflowTestSuite;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyShell;

public class TestSuiteBuilder {

	static ITestSuite nextflow_pipeline(
			@DelegatesTo(value = PipelineTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final PipelineTestSuite suite = new PipelineTestSuite();

		closure.setDelegate(suite);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return suite;

	}

	static ITestSuite nextflow_workflow(
			@DelegatesTo(value = PipelineTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final WorkflowTestSuite suite = new WorkflowTestSuite();

		closure.setDelegate(suite);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return suite;

	}

	static ITestSuite nextflow_process(
			@DelegatesTo(value = PipelineTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final ProcessTestSuite suite = new ProcessTestSuite();

		closure.setDelegate(suite);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return suite;

	}

	static ITestSuite nextflow_function(
			@DelegatesTo(value = PipelineTestSuite.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final FunctionTestSuite suite = new FunctionTestSuite();

		closure.setDelegate(suite);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		return suite;

	}

	public static ITestSuite parse(File script) throws Exception {
		return parse(script, "");
	}

	public static ITestSuite parse(File script, String libDir) throws Exception {

		ImportCustomizer customizer = new ImportCustomizer();
		customizer.addStaticImport("com.askimed.nf.test.lang.TestSuiteBuilder", "nextflow_pipeline");
		customizer.addStaticImport("com.askimed.nf.test.lang.TestSuiteBuilder", "nextflow_workflow");
		customizer.addStaticImport("com.askimed.nf.test.lang.TestSuiteBuilder", "nextflow_process");
		customizer.addStaticImport("com.askimed.nf.test.lang.TestSuiteBuilder", "nextflow_function");
		customizer.addStaticStars("com.askimed.nf.test.lang.extensions.GlobalMethods");

		PluginManager manager = PluginManager.getInstance();
		for (String staticImport : manager.getStaticImports()) {
			customizer.addStaticStars(staticImport);
		}

		CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
		String classpath = script.getAbsoluteFile().getParentFile().getAbsolutePath() + "/lib:" + libDir;
		compilerConfiguration.setClasspath(classpath);

		compilerConfiguration.addCompilationCustomizers(customizer);

		GroovyShell shell = new GroovyShell(manager.getClassLoader(), compilerConfiguration);

		Object object = shell.evaluate(script);

		if (!(object instanceof ITestSuite)) {
			throw new Exception("Not a valid TestSuite object.");
		}

		ITestSuite testSuite = (ITestSuite) object;
		testSuite.setFilename(script.getAbsolutePath());

		return testSuite;
	}

}