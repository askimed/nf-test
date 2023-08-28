package com.askimed.nf.test.lang.process;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.lang.TestCode;
import com.askimed.nf.test.nextflow.NextflowCommand;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.FileUtil;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class ProcessTest extends AbstractTest {

	private String name = "Unknown test";

	private boolean autoSort = true;

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private ProcessTestSuite parent;

	private ProcessContext context;

	public ProcessTest(ProcessTestSuite parent) {
		super(parent);
		this.parent = parent;
		this.autoSort = parent.isAutoSort();
		context = new ProcessContext(this);
		context.setName(parent.getProcess());
	}

	public void name(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setup(@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new TestCode(closure);
	}

	public void cleanup(
			@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		cleanup = new TestCode(closure);
	}

	public void then(@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		then = new TestCode(closure);
	}

	public void when(@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		when = new TestCode(closure);
	}

	public void debug(boolean debug) {
		setDebug(debug);
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
		context.evaluateProcessClosure();

		// Create workflow mock
		File workflow = new File(metaDir, "mock.nf");
		writeWorkflowMock(workflow);

		context.getParams().put("nf_test_output", metaDir.getAbsolutePath());

		if (isDebug()) {
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
		if (isWithTrace()) {
			nextflow.setTrace(traceFile);
		}
		nextflow.setOut(outFile);
		nextflow.setErr(errFile);
		nextflow.setSilent(!isDebug());
		nextflow.setLog(logFile);
		nextflow.setWork(workDir);
		nextflow.setParamsFile(paramsFile);
		nextflow.setOptions(getOptions());

		int exitCode = nextflow.execute();

		// Parse json output
		context.getProcess().loadOutputChannels(metaDir, autoSort);
		context.getProcess().loadFromFolder(metaDir);
		context.getProcess().exitStatus = exitCode;
		context.getProcess().success = (exitCode == 0);
		context.getProcess().failed = (exitCode != 0);

		context.getWorkflow().loadFromFolder(metaDir);
		context.getWorkflow().exitStatus = exitCode;
		context.getWorkflow().success = (exitCode == 0);
		context.getWorkflow().failed = (exitCode != 0);
		if (isDebug()) {
			System.out.println(AnsiText.padding("Output Channels:", 4));
			context.getProcess().viewChannels();
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

		Map<Object, Object> binding = new HashMap<Object, Object>();
		binding.put("process", parent.getProcess());
		binding.put("script", script);

		// Get body of when closure
		binding.put("mapping", context.getProcess().getMapping());

		URL templateUrl = this.getClass().getResource("WorkflowMock.nf");
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);

		FileUtil.write(file, template);

	}

}