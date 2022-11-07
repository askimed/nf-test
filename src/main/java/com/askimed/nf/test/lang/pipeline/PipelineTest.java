package com.askimed.nf.test.lang.pipeline;

import java.io.File;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.lang.TestCode;
import com.askimed.nf.test.lang.TestContext;
import com.askimed.nf.test.nextflow.NextflowCommand;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class PipelineTest extends AbstractTest {

	private String name = "Unknown test";

	private boolean debug = false;

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private PipelineTestSuite parent;

	private TestContext context;

	private boolean withTrace = true;

	public PipelineTest(PipelineTestSuite parent) {
		super();
		this.parent = parent;
		context = new TestContext(this);
		context.setName(parent.getName());
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

	public void expect(
			@DelegatesTo(value = PipelineTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		then = new TestCode(closure);
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

		context.init(baseDir, outputDir.getAbsolutePath());
		
		if (setup != null) {
			setup.execute(context);
		}

		if (when != null) {
			when.execute(context);
		}

		context.evaluateParamsClosure();
		context.evaluateProcessClosure();

		if (debug) {
			System.out.println();
		}

		File traceFile = new File(metaDir, "trace.csv");
		File outFile = new File(metaDir, "std.out");
		File errFile = new File(metaDir, "std.err");
		File logFile = new File(metaDir, "nextflow.log");
		File paramsFile = new File(metaDir, "params.json");

		NextflowCommand nextflow = new NextflowCommand();
		nextflow.setScript(parent.getScript());
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

	@Override
	public void setWithTrace(boolean withTrace) {
		this.withTrace = withTrace;
	}

}