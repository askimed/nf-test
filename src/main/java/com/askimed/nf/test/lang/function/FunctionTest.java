package com.askimed.nf.test.lang.function;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.lang.TestCode;
import com.askimed.nf.test.nextflow.NextflowCommand;
import com.askimed.nf.test.util.FileUtil;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class FunctionTest extends AbstractTest {

	private String name = "Unknown test";

	private String function = null;

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private FunctionContext context;

	private FunctionTestSuite parent;

	public FunctionTest(FunctionTestSuite parent) {
		super(parent);
		this.parent = parent;
		context = new FunctionContext(this);
		context.setName(parent.getFunction());
	}

	public void name(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void function(String function) {
		this.function = function;
	}

	public String getFunction() {
		return function;
	}

	public void setup(
			@DelegatesTo(value = FunctionTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new TestCode(closure);
	}

	public void cleanup(
			@DelegatesTo(value = FunctionTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		cleanup = new TestCode(closure);
	}

	public void then(@DelegatesTo(value = FunctionTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		then = new TestCode(closure);
	}

	public void when(@DelegatesTo(value = FunctionTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		when = new TestCode(closure);
	}

	@Override
	public void execute() throws Throwable {

		if (parent.getScript() != null) {

			File script = new File(parent.getScript());

			if (!script.exists()) {
				throw new Exception("Script '" + script.getAbsolutePath() + "' not found.");
			}
		}

		context.init(this);

		if (setup != null) {
			setup.execute(context);
		}

		if (when != null) {
			when.execute(context);
		}

		context.evaluateParamsClosure();
		context.evaluateFunctionClosure();

		if (isDebug()) {
			System.out.println();
		}

		// Create workflow mock
		File workflow = new File(metaDir, FILE_MOCK);
		writeWorkflowMock(workflow);

		context.getParams().put("nf_test_output", metaDir.getAbsolutePath());

		File traceFile = new File(metaDir, FILE_TRACE);
		File outFile = new File(metaDir, FILE_STD_OUT);
		File errFile = new File(metaDir, FILE_STD_ERR);
		File logFile = new File(metaDir, FILE_NEXTFLOW_LOG);
		File paramsFile = new File(metaDir, FILE_PARAMS);

		NextflowCommand nextflow = new NextflowCommand();
		nextflow.setScript(workflow.getAbsolutePath());
		nextflow.setParams(context.getParams());
		nextflow.setProfile(parent.getProfile());
		nextflow.addConfig(parent.getGlobalConfigFile());
		nextflow.addConfig(parent.getLocalConfig());
		nextflow.addConfig(getConfig());
		if (isWithTrace()) {
			nextflow.setTrace(traceFile);
		}
		nextflow.setOut(outFile);
		nextflow.setErr(errFile);
		nextflow.setSilent(!isDebug());
		nextflow.setLog(logFile);
		nextflow.setLaunchDir(launchDir);
		nextflow.setWorkDir(workDir);
		nextflow.setParamsFile(paramsFile);
		nextflow.setOptions(getOptions());

		int exitCode = nextflow.execute();

		// Parse json output
		context.getFunction().loadResult(metaDir);
		context.getFunction().loadFromFolder(metaDir);
		context.getFunction().exitStatus = exitCode;
		context.getFunction().success = (exitCode == 0);
		context.getFunction().failed = (exitCode != 0);

		context.getWorkflow().loadFromFolder(metaDir);
		context.getWorkflow().exitStatus = exitCode;
		context.getWorkflow().success = (exitCode == 0);
		context.getWorkflow().failed = (exitCode != 0);

		then.execute(context);

	}

	public void cleanup() {
		if (cleanup != null) {
			cleanup.execute(context);
		}
	}

	protected void writeWorkflowMock(File file) throws IOException, CompilationFailedException, ClassNotFoundException {

		String script = parent.getScript();

		if (script != null && !script.startsWith("/") && !script.startsWith("./")) {
			script = new File(script).getAbsolutePath();
		}

		String name = function != null ? function : parent.getFunction();
		String include = name;
		// if function is a static method: include class.
		if (name.contains(".")) {
			String[] tiles = name.split("\\.", 2);
			include = tiles[0];
		}

		Map<Object, Object> binding = new HashMap<Object, Object>();
		binding.put("function", name);
		binding.put("include", include);
		binding.put("script", script);

		// Get body of when closure
		binding.put("mapping", context.getFunction().getMapping());

		URL templateUrl = this.getClass().getResource("WorkflowMock.nf");
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);

		FileUtil.write(file, template);

	}

}