package com.askimed.nf.test.lang.function;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.lang.TestCode;
import com.askimed.nf.test.lang.TestContext;
import com.askimed.nf.test.nextflow.NextflowCommand;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.FileUtil;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class FunctionTest extends AbstractTest {

	private String name = "Unknown test";

	private String function = null;

	private boolean debug = false;

	private boolean withTrace = true;

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private FunctionTestSuite parent;

	private TestContext context;

	public FunctionTest(FunctionTestSuite parent) {
		super();
		this.parent = parent;
		context = new TestContext(this);
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

	public void debug(boolean debug) {
		setDebug(debug);
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void execute() throws Throwable {

		if (parent.getScript() != null) {

			File script = new File(parent.getScript());

			if (!script.exists()) {
				throw new Exception("Script '" + script.getAbsolutePath() + "' not found.");
			}
		}

		context.init(baseDir, outputDir.getAbsolutePath());
		
		if (setup != null) {
			setup.execute(context);
		}


		if (when != null) {
			when.execute(context);
		}

		context.evaluateParamsClosure();
		context.evaluateFunctionClosure();

		// Create workflow mock
		File workflow = new File(metaDir, "mock.nf");
		writeWorkflowMock(workflow);

		context.getParams().put("nf_test_output", metaDir.getAbsolutePath());

		File traceFile = new File(metaDir, "trace.csv");
		File outFile = new File(metaDir, "std.out");
		File errFile = new File(metaDir, "std.err");
		File logFile = new File(metaDir, "nextflow.log");
		File paramsFile = new File(metaDir, "params.json");

		NextflowCommand nextflow = new NextflowCommand();
		nextflow.setScript(workflow.getAbsolutePath());
		nextflow.setParams(context.getParams());
		nextflow.setProfile(parent.getProfile());
		nextflow.addConfig(parent.getGlobalConfigFile());
		nextflow.addConfig(parent.getLocalConfig());
		nextflow.addConfig(getConfig());
		if (withTrace) {
			nextflow.setTrace(traceFile);
		}
		nextflow.setOut(outFile);
		nextflow.setErr(errFile);
		nextflow.setSilent(!debug);
		nextflow.setLog(logFile);
		nextflow.setWork(workDir);
		nextflow.setParamsFile(paramsFile);

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
		if (debug) {
			System.out.println(AnsiText.padding("Output Channels:", 4));
			context.getProcess().getOut().view();
		}

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
		binding.put("mapping", context.getProcess().getMapping());

		URL templateUrl = this.getClass().getResource("WorkflowMock.nf");
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);

		FileUtil.write(file, template);

	}

	@Override
	public void setWithTrace(boolean withTrace) {
		this.withTrace = withTrace;
	}

}