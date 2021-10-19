package com.github.lukfor.testflight.lang;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import com.github.lukfor.testflight.core.ITest;
import com.github.lukfor.testflight.core.NextflowCommand;

import groovy.json.JsonOutput;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class NextflowTest implements ITest {

	private String name;

	private boolean debug;

	private NextflowTestCode setup;

	private NextflowTestCode cleanup;

	private NextflowTestCode when;

	private NextflowTestCode then;

	private NextflowTestSuite parent;

	private NextflowTestContext context;

	public NextflowTest(NextflowTestSuite parent) {
		this.parent = parent;
		context = new NextflowTestContext();
	}

	public void name(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setup(
			@DelegatesTo(value = NextflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new NextflowTestCode(closure);
	}

	public void cleanup(
			@DelegatesTo(value = NextflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		cleanup = new NextflowTestCode(closure);
	}

	public void when(@DelegatesTo(value = NextflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		when = new NextflowTestCode(closure);
	}

	public void then(@DelegatesTo(value = NextflowTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		then = new NextflowTestCode(closure);
	}

	public void debug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void execute() throws Throwable {

		if (setup != null) {
			setup.execute(context);
		}

		when.execute(context);

		NextflowCommand nextflow = new NextflowCommand();
		nextflow.setScript(parent.getScript());
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

	protected void writeParamsJson(Map<String, Object> params, String filename) throws IOException {

		BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
		writer.write(JsonOutput.toJson(params));
		writer.close();

	}

}