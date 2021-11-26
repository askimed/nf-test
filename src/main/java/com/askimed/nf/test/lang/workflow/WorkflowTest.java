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
import com.askimed.nf.test.nextflow.NextflowCommand;
import com.askimed.nf.test.util.FileUtil;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class WorkflowTest extends AbstractTest {

	private String name = "Unknown test";

	private boolean debug = false;

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private WorkflowTestSuite parent;

	private TestContext context;

	public WorkflowTest(WorkflowTestSuite parent) {
		super();
		this.parent = parent;
		context = new TestContext();
		context.setName(parent.getWorkflow());
	}

	public void name(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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

	public void debug(boolean debug) {
		setDebug(debug);
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void execute() throws Throwable {

		File script = new File(parent.getScript());

		if (!script.exists()) {
			throw new Exception("Script '" + script.getAbsolutePath() + "' not found.");
		}

		if (setup != null) {
			setup.execute(context);
		}

		when.execute(context);

		// Create workflow mock
		File workflow = new File("test_mock.nf");
		writeWorkflowMock(workflow);

		context.getParams().put("nf_testflight_output", metaDir.getAbsolutePath());
		if (debug) {
			System.out.println();
		}

		File traceFile = new File(metaDir, "trace.csv");

		NextflowCommand nextflow = new NextflowCommand();
		nextflow.setScript(workflow.getAbsolutePath());
		nextflow.setParams(context.getParams());
		nextflow.setProfile(parent.getProfile());
		nextflow.setConfig(parent.getConfig());
		nextflow.setTrace(traceFile);
		nextflow.setSilent(!debug);
		int exitCode = nextflow.execute();

		workflow.delete();

		context.getWorkflow().loadFromFolder(metaDir);
		context.getWorkflow().exitStatus = exitCode;

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
			script = "./" + script;
		}

		Map<Object, Object> binding = new HashMap<Object, Object>();
		binding.put("workflow", parent.getWorkflow());
		binding.put("script", script);

		// Get body of when closure
		binding.put("mapping", context.getWorkflow().getMapping());

		URL templateUrl = this.getClass().getResource("WorkflowMock.nf");
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);

		FileUtil.write(file, template);

	}

}