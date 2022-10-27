package com.askimed.nf.test.lang.workflow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.lang.TestCode;
import com.askimed.nf.test.lang.TestContext;
import com.askimed.nf.test.lang.pipeline.PipelineTest;
import com.askimed.nf.test.nextflow.NextflowCommand;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.FileUtil;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class WorkflowTest extends AbstractTest {

	private String name = "Unknown test";

	private boolean debug = false;

	private boolean autoSort = true;

	private boolean withTrace = true;

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private String workflow = null;

	private WorkflowTestSuite parent;

	private TestContext context;

	public WorkflowTest(WorkflowTestSuite parent) {
		super();
		this.parent = parent;

		context = new TestContext(this);
		context.setName(parent.getWorkflow());
	}

	public void name(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void workflow(String workflow) {
		this.workflow = workflow;
	}

	public String getWorkflow() {
		return workflow;
	}

	public void setup(
			@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new TestCode(closure);
	}

	public void cleanup(
			@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		cleanup = new TestCode(closure);
	}

	public void then(@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		then = new TestCode(closure);
	}

	public void when(@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		when = new TestCode(closure);
	}
	
	public void expect(
			@DelegatesTo(value = PipelineTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		then = new TestCode(closure);
	}

	public void debug(boolean debug) {
		setDebug(debug);
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void autoSort(boolean autoSort) {
		this.autoSort = autoSort;
	}

	@Override
	public void execute() throws Throwable {

		File script = new File(parent.getScript());

		if (!script.exists()) {
			throw new Exception("Script '" + script.getAbsolutePath() + "' not found.");
		}

		context.init(baseDir, outputDir.getAbsolutePath());
		
		if (setup != null) {
			setup.execute(context);
		}
		
		if (when != null) {
			when.execute(context);
		}

		context.evaluateParamsClosure();
		context.evaluateWorkflowClosure();

		// Create workflow mock
		File workflow = new File(metaDir, "mock.nf");
		writeWorkflowMock(workflow);

		context.getParams().put("nf_test_output", metaDir.getAbsolutePath());
		if (debug) {
			System.out.println();
		}

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

		context.getWorkflow().getOut().loadFromFolder(metaDir, autoSort);
		context.getWorkflow().loadFromFolder(metaDir);
		context.getWorkflow().exitStatus = exitCode;
		context.getWorkflow().success = (exitCode == 0);
		context.getWorkflow().failed = (exitCode != 0);

		if (debug) {
			System.out.println(AnsiText.padding("Output Channels:", 4));
			context.getWorkflow().getOut().view();
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

		if (!script.startsWith("/") && !script.startsWith("./")) {
			script = new File(script).getAbsolutePath();
		}

		String name = workflow != null ? workflow : parent.getWorkflow();

		Map<Object, Object> binding = new HashMap<Object, Object>();
		binding.put("workflow", name);
		binding.put("script", script);

		// Get body of when closure
		binding.put("mapping", context.getWorkflow().getMapping());

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