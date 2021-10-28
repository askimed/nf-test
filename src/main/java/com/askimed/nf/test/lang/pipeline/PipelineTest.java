package com.askimed.nf.test.lang.pipeline;

import java.io.File;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.TestCode;
import com.askimed.nf.test.lang.TestContext;
import com.askimed.nf.test.nextflow.NextflowCommand;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class PipelineTest implements ITest {

	private String name = "Unknown test";

	private boolean debug = false;

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private PipelineTestSuite parent;

	private TestContext context;

	public PipelineTest(PipelineTestSuite parent) {
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
			@DelegatesTo(value = PipelineTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new TestCode(closure);
	}

	public void cleanup(
			@DelegatesTo(value = PipelineTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		cleanup = new TestCode(closure);
	}

	public void when(@DelegatesTo(value = PipelineTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		when = new TestCode(closure);
	}

	public void then(@DelegatesTo(value = PipelineTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		then = new TestCode(closure);
	}

	public void debug(boolean debug) {
		this.debug = debug;
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

		if (debug) {
			System.out.println();
		}

		NextflowCommand nextflow = new NextflowCommand();
		nextflow.setScript(script);
		nextflow.setParams(context.getParams());
		nextflow.setProfile(parent.getProfile());
		nextflow.setConfig(parent.getConfig());
		nextflow.setSilent(!debug);
		int exitCode = nextflow.execute();

		context.getWorkflow().setExitCode(exitCode);

		then.execute(context);

	}

	public void cleanup() {
		if (cleanup != null) {
			cleanup.execute(context);
		}
	}

}