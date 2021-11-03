package com.askimed.nf.test.lang.process;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.TestCode;
import com.askimed.nf.test.lang.TestContext;
import com.askimed.nf.test.nextflow.NextflowCommand;
import com.askimed.nf.test.util.AnsiText;
import com.askimed.nf.test.util.FileUtil;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;

public class ProcessTest implements ITest {

	private String name = "Unknown test";

	private boolean debug = false;

	private boolean autoSort = true;

	private TestCode setup;

	private TestCode cleanup;

	private TestCode when;

	private TestCode then;

	private ProcessTestSuite parent;

	private TestContext context;

	public ProcessTest(ProcessTestSuite parent) {
		this.parent = parent;
		context = new TestContext();
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

		if (setup != null) {
			setup.execute(context);
		}

		when.execute(context);

		// Create workflow mock
		File workflow = new File("test_mock.nf");
		writeWorkflowMock(workflow);

		File jsonFolder = new File("json");
		FileUtil.deleteDirectory(jsonFolder);
		FileUtil.createDirectory(jsonFolder);

		context.getParams().put("nf_testflight_output", jsonFolder.getAbsolutePath());

		if (debug) {
			System.out.println();
		}

		NextflowCommand nextflow = new NextflowCommand();
		nextflow.setScript(workflow);
		nextflow.setParams(context.getParams());
		nextflow.setProfile(parent.getProfile());
		nextflow.setConfig(parent.getConfig());
		nextflow.setSilent(!debug);
		int exitCode = nextflow.execute();

		workflow.delete();

		// Parse json output
		context.getProcess().getOut().loadFromFolder(jsonFolder, autoSort);
		context.getProcess().loadFromFolder(jsonFolder);
		context.getWorkflow().loadFromFolder(jsonFolder);
		context.getWorkflow().exitStatus = exitCode;

		if (debug) {
			System.out.println(AnsiText.padding("Output Channels:", 4));
			context.getProcess().getOut().view();
		}

		// delete jsonFolder
		FileUtil.deleteDirectory(jsonFolder);

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