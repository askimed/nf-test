package com.askimed.nf.test.lang.pipeline;

import java.io.File;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.lang.TestCode;
import com.askimed.nf.test.nextflow.NextflowCommand;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class PipelineTest extends AbstractTest {

	private String name = "Unknown test";

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private PipelineTestSuite parent;

	private PipelineContext context;

	public PipelineTest(PipelineTestSuite parent) {
		super(parent);
		this.parent = parent;
		context = new PipelineContext(this);
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

	@Override
	public void execute() throws Throwable {

		super.execute();
		
		context.init(this);

		if (setup != null) {
			setup.execute(context);
		}

		if (when != null) {
			when.execute(context);
		}

		context.evaluateParamsClosure();

		if (isDebug()) {
			System.out.println();
		}

		File traceFile = new File(metaDir, FILE_TRACE);
		File outFile = new File(metaDir, FILE_STD_OUT);
		File errFile = new File(metaDir, FILE_STD_ERR);
		File logFile = new File(metaDir, FILE_NEXTFLOW_LOG);
		File paramsFile = new File(metaDir, FILE_PARAMS);

		String script = parent.getScript();

		if (!script.startsWith("/") && !script.startsWith("./")) {
			script = new File(script).getAbsolutePath();
		}
		
		//file not found. try as github location
		if (!new File(script).exists()) {
			script = parent.getScript();
		}
		
		NextflowCommand nextflow = new NextflowCommand();
		nextflow.setScript(script);
		nextflow.setParams(context.getParams());
		nextflow.setProfile(parent.getProfile());
		File projectConfig = new File("nextflow.config");
		if (projectConfig.exists()) {
			nextflow.addConfig(projectConfig);	
		}
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

}