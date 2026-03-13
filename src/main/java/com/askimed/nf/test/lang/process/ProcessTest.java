package com.askimed.nf.test.lang.process;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Vector;

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

/**
 * ProcessTest is a class that represents a test case for a nextflow process. It contains the test code and the context of the test case.
 * The context contains the process definition and the dependencies to be executed before the test case.
 */
public class ProcessTest extends AbstractTest {

	/**
	 * The name of the test case.
	 */
	private String name = "Unknown test";

	/**
	 * Flag to indicate if the output channels should be sorted by name before being checked in the test case.
	 */
	private boolean autoSort = true;

	/**
	 * The setup code to be executed before the test case.
	 */
	private TestCode setup;

	/**
	 * The cleanup code to be executed after the test case.
	 */
	private TestCode cleanup;

	/**
	 * The code to be executed to provide the input for the test case.
	 */
	private TestCode when;

	/**
	 * The code to be executed to check the output of the test case.
	 */
	private TestCode then;

	/**
	 * The test suite this current test is part of.
	 */
	private ProcessTestSuite parent;

	/**
	 * The context of the test case. It contains the process definition and the dependencies to be executed before the test case.
	 */
	private ProcessContext context;

	/**
	 * The list of topics channel names to be checked for in the test case. 
	 * This is inherited from the test suite, but can be expanded by the test itself.
	 */
	private List<String> topics = new Vector<String>();

	/**
	 * Create a new process test case.
	 * @param parent The test suite this test case belongs to.
	 */
	public ProcessTest(ProcessTestSuite parent) {
		super(parent);
		this.parent = parent;
		this.topics = new Vector<String>(parent.getTopics());
		this.autoSort = parent.isAutoSort();
		context = new ProcessContext(this);
		context.setName(parent.getProcess());
	}

	/**
	 * Set the name of the test case.
	 * @param name The name of the test case
	 */
	public void name(String name) {
		this.name = name;
	}

	/**
	 * Get the name of the test case.
	 * @return The name of the test case
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the setup code to be executed before the test case.
	 * @param closure The closure containing the setup code to be executed before the test case.
	 */
	public void setup(@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new TestCode(closure);
	}

	/**
	 * Set the cleanup code to be executed after the test case.
	 * @param closure The closure containing the cleanup code to be executed after the test case.
	 */
	public void cleanup(
			@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		cleanup = new TestCode(closure);
	}

	/**
	 * Set the code to be executed to test the output of the test case.
	 * @param closure The closure containing the code to be executed to test the output of the test case.
	 */
	public void then(@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		then = new TestCode(closure);
	}

	/**
	 * Set the code to be executed to provide the input for the test case.
	 * @param closure The closure containing the code to be executed to provide the input for the test case.
	 */
	public void when(@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		when = new TestCode(closure);
	}

	/**
	 * Set the debug flag to indicate if the test case should be executed in debug mode.
	 * @param debug The debug flag to indicate if the test case should be executed in debug mode.
	 */
	public void debug(boolean debug) {
		setDebug(debug);
	}

	/**
	 * Set the flag to indicate if the output channels should be sorted by name before being checked in the test case.
	 * @param autoSort The flag to indicate if the output channels should be sorted by name
	 */
	public void autoSort(boolean autoSort) {
		this.autoSort = autoSort;
	}

	/**
	 * Expand the topic channel names to be checked by a list specified for this specific test.
	 * @param topics A list of topic channel names
	 */
	public void topics(String... topics) {
		for (String topic : topics) {
			this.topics.add(topic);
		}
	}

	/**
	 * Execute the test case. This method is called by the test suite to execute the test case. It executes the setup code, the when code, runs nextflow and then executes the then code.
	 * @throws Throwable If an error occurs while executing the test case.
	 */
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

		context.getTopics().loadFromFolder(metaDir, autoSort, "topic_");
		if (isDebug()) {
			System.out.println(AnsiText.padding("Output Channels:", 4));
			context.getProcess().viewChannels();
			System.out.println(AnsiText.padding("Topics Channels:", 4));
			context.getTopics().view();
		}

		then.execute(context);

	}

	/**
	 * Cleanup the test case. This method is called by the test suite to cleanup after the test case. It executes the cleanup code.
	 * @throws Throwable If an error occurs while executing the cleanup code.
	 */
	public void cleanup() {
		if (cleanup != null) {
			cleanup.execute(context);
		}
	}

	/**
	 * Write the workflow mock file. This file is used to execute the test case. It contains the process definition and the dependencies to be executed before the test case.
	 * @param file The file to write the workflow mock to.
	 * @throws IOException If an error occurs while writing the file.
	 * @throws CompilationFailedException If an error occurs while compiling the workflow mock.
	 * @throws ClassNotFoundException If a class required by the workflow mock cannot be found.
	 */
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
		binding.put("topics", topics);

		// Get body of when closure
		binding.put("mapping", context.getProcess().getMapping());

		URL templateUrl = this.getClass().getResource("WorkflowMock.nf");
		SimpleTemplateEngine engine = new SimpleTemplateEngine();
		Writable template = engine.createTemplate(templateUrl).make(binding);

		FileUtil.write(file, template);

	}

}