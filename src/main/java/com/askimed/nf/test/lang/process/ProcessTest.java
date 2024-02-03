package com.askimed.nf.test.lang.process;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.lang.Dependency;
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

		super.execute();

		File script = new File(parent.getScript());

		if (!script.exists()) {
			throw new Exception("Script '" + script.getAbsolutePath() + "' not found.");
		}

		context.init(this);

		if (parent.getSetup() != null) {
			parent.getSetup().execute(context);
		}
		
		if (setup != null) {
			setup.execute(context);
		}

		if (when != null) {
			when.execute(context);
		}

		context.evaluateParamsClosure();
		context.evaluateProcessClosure();

		// Create workflow mock
		writeWorkflowMock(mockFile);

		// Copy mock file in meta folder for debugging
		FileUtil.copy(mockFile, new File(metaDir, FILE_MOCK));

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
		nextflow.setScript(mockFile.getAbsolutePath());
		nextflow.setParams(context.getParams());
		for (String profile: parent.getProfiles()) {
			nextflow.addProfile(profile);
		}
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
		nextflow.setDebug(isDebug());
		nextflow.setLog(logFile);
		nextflow.setLaunchDir(launchDir);
		nextflow.setWorkDir(workDir);
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

		// update dependency paths
		for (Dependency dependency : context.getDependencies()) {
			String _script = dependency.getScript();
			if (_script == null) {
				dependency.setScript(script);
			} else {
				if (parent.isRelative(_script)) {
					_script = parent.makeAbsolute(_script);
				}
				if (!_script.startsWith("/") && !_script.startsWith("./")) {
					_script = new File(_script).getAbsolutePath();
				}
				dependency.setScript(_script);
			}
		}

		Map<Object, Object> binding = new HashMap<Object, Object>();
		binding.put("process", parent.getProcess());
		binding.put("script", script);
		binding.put("dependencies", context.getDependencies());

		// Get body of when closure
		binding.put("mapping", context.getProcess().getMapping());

		URL templateUrl = this.getClass().getResource("WorkflowMock.nf");
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);

		FileUtil.write(file, template);

	}

}