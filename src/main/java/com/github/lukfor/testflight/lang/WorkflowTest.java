package com.github.lukfor.testflight.lang;

import java.io.File;
import java.nio.file.Paths;

import com.github.lukfor.testflight.core.ITest;
import com.github.lukfor.testflight.nextflow.NextflowCommand;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class WorkflowTest implements ITest {

	private String name;

	private boolean debug;

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private WorkflowTestSuite parent;

	private TestContext context;

	public WorkflowTest(WorkflowTestSuite parent) {
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
			@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new TestCode(closure);
	}

	public void cleanup(
			@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		cleanup = new TestCode(closure);
	}

	public void when(@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		when = new TestCode(closure);
	}

	public void then(@DelegatesTo(value = WorkflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
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