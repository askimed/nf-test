package com.github.lukfor.testflight.lang;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.github.lukfor.testflight.core.ITest;
import com.github.lukfor.testflight.nextflow.NextflowCommand;

import groovy.json.JsonOutput;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class ProcessTest implements ITest {

	private String name;

	private boolean debug;

	private WorkflowTestCode setup;

	private WorkflowTestCode cleanup;

	private WorkflowTestCode when;

	private WorkflowTestCode then;

	private ProcessTestSuite parent;

	private TestContext context;

	public ProcessTest(ProcessTestSuite parent) {
		this.parent = parent;
		context = new TestContext();
	}

	public void name(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setup(
			@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new WorkflowTestCode(closure);
	}

	public void cleanup(
			@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		cleanup = new WorkflowTestCode(closure);
	}

	public void when(
			@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		when = new WorkflowTestCode(closure);
	}

	public void then(
			@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		then = new WorkflowTestCode(closure);
	}

	public void debug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void execute() throws Throwable {

		if (setup != null) {
			setup.execute(context);
		}

		// Create template

		// Copy to template
		when.execute(context);

		NextflowCommand nextflow = new NextflowCommand();
		nextflow.setScript(parent.getScript());
		nextflow.setParams(context.getParams());
		nextflow.setProfile(parent.getProfile());
		nextflow.setSilent(!debug);
		int exitCode = nextflow.execute();

		// Parse json output

		context.getWorkflow().setExitCode(exitCode);

		then.execute(context);

	}

	public void cleanup() {
		if (cleanup != null) {
			cleanup.execute(context);
		}
	}

	protected void writeParamsJson(Map<String, Object> params, String filename) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		writer.write(JsonOutput.toJson(params));
		writer.close();

	}

}