package com.askimed.nf.test.lang.workflow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.lang.TestCode;
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

	private boolean autoSort = true;

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private String workflow = null;

	private WorkflowTestSuite parent;

	private WorkflowContext context;

	public WorkflowTest(WorkflowTestSuite parent) {
		super(parent);
		this.parent = parent;
		this.autoSort = parent.isAutoSort();
		context = new WorkflowContext(this);
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

	public void autoSort(boolean autoSort) {
		this.autoSort = autoSort;
	}

	@Override
	public void execute() throws Throwable {

		super.execute();

		
		File script = new File(parent.getScript());

		if (!script.exists()) {
			throw new Exception("Script '" + script.getAbsolutePath() + "' not found.");
		}
		
		context.init(this);

		if (setup != null) {
			setup.execute(context);
		}

		if (when != null) {
			when.execute(context);
		}

		context.evaluateParamsClosure();
		context.evaluateWorkflowClosure();

		// Create workflow mock
		File workflow = new File(metaDir, FILE_MOCK);
		writeWorkflowMock(workflow);

		context.getParams().put("nf_test_output", metaDir.getAbsolutePath());
		if (isDebug()) {
			System.out.println();
		}

		File traceFile = new File(metaDir, FILE_TRACE);
		File outFile = new File(metaDir, FILE_STD_OUT);
		File errFile = new File(metaDir, FILE_STD_ERR);
		File logFile = new File(metaDir, FILE_NEXTFLOW_LOG);
		File paramsFile = new File(metaDir, FILE_PARAMS);

		NextflowCommand nextflow = new NextflowCommand();
		nextflow.setScript(workflow.getAbsolutePath());
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

		context.getWorkflow().loadOutputChannels(metaDir, autoSort);
		context.getWorkflow().loadFromFolder(metaDir);
		context.getWorkflow().exitStatus = exitCode;
		context.getWorkflow().success = (exitCode == 0);
		context.getWorkflow().failed = (exitCode != 0);

		if (isDebug()) {
			System.out.println(AnsiText.padding("Output Channels:", 4));
			context.getWorkflow().viewChannels();
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

}