package com.askimed.nf.test.lang;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.control.CompilationFailedException;

import com.askimed.nf.test.core.AbstractTest;
import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.extensions.Snapshot;
import com.askimed.nf.test.lang.workflow.Workflow;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;

public class TestContext extends GroovyObjectSupport {

	private ParamsMap params;

	private Closure paramsClosure;

	public String baseDir;

	public String projectDir;

	public String launchDir;

	public String workDir;

	public String outputDir;

	public String moduleDir;

	public String moduleTestDir;

	public ITest test;

	private WorkflowMeta workflow = new WorkflowMeta();

	private Map<String, Object> testParameters = new HashMap<String, Object>();

	public TestContext(ITest test) {
		params = new ParamsMap(this);
		this.test = test;
		// Initialize test parameters from the test
		if (test != null && test.getParameters() != null) {
			this.testParameters = new HashMap<String, Object>(test.getParameters());
		}
	}

	public void init(AbstractTest test) {

		this.baseDir = test.baseDir.getAbsolutePath();
		this.projectDir = test.baseDir.getAbsolutePath();
		this.launchDir = test.launchDir.getAbsolutePath();
		this.workDir = test.workDir.getAbsolutePath();
		this.outputDir = test.outputDir.getAbsolutePath();
		if (test.moduleDir != null) {
			this.moduleDir = test.moduleDir.getAbsolutePath();
		}
		if (test.moduleTestDir != null) {
			this.moduleTestDir = test.moduleTestDir.getAbsolutePath();
		}
	}

	public ParamsMap getParams() {
		return params;
	}

	public void setParams(ParamsMap params) {
		this.params = params;
	}

	public void params(Closure closure) {
		this.paramsClosure = closure;
	}

	public void evaluateParamsClosure() {

		if (paramsClosure == null) {
			return;
		}
		Closure newClosure = paramsClosure.rehydrate(params, this, this);
		newClosure.call();
		params.evaluateNestedClosures();

	}

	public WorkflowMeta getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		throw new RuntimeException("Variable 'workflow' is read only");
	}

	public Snapshot snapshot(Object... object) {
		return new Snapshot(object, test);
	}

	public void loadParams(String filename) throws CompilationFailedException, ClassNotFoundException, IOException {
		params.load(filename);
	}

	public void setOutputDir(String outputDir) {
		// The 'outputDir' variable is read-only. If an update occurs within the
		// 'params' closure, update the 'outputDir' key in the hashmap instead of
		// modifying the variable itself. This fixes issue 125.
		params.put("outputDir", outputDir);
	}

	public void setBaseDir(String baseDir) {
		throw new RuntimeException("Variable 'baseDir' is read only");
	}

	public void setLaunchDir(String launchDir) {
		throw new RuntimeException("Variable 'launchDir' is read only");
	}

	public void setModuleDir(String moduleDir) {
		throw new RuntimeException("Variable 'moduleDir' is read only");
	}

	public void setModuleTestDir(String moduleTestDir) {
		throw new RuntimeException("Variable 'moduleTestDir' is read only");
	}

	public void setProjectDir(String projectDir) {
		throw new RuntimeException("Variable 'projectDir' is read only");
	}

	public void setWorkDir(String workDir) {
		throw new RuntimeException("Variable 'workDir' is read only");
	}

	public void setTestParameters(Map<String, Object> parameters) {
		if (parameters != null) {
			this.testParameters = new HashMap<String, Object>(parameters);
		}
	}

	public Map<String, Object> getTestParameters() {
		return testParameters;
	}

	@Override
	public Object getProperty(String name) {
		// First check if it's a test parameter
		if (testParameters.containsKey(name)) {
			return testParameters.get(name);
		}
		// Fall back to default Groovy property resolution
		return super.getProperty(name);
	}

	@Override
	public void setProperty(String name, Object value) {
		// Allow setting test parameters dynamically
		if (testParameters.containsKey(name)) {
			testParameters.put(name, value);
		} else {
			// Fall back to default Groovy property resolution
			super.setProperty(name, value);
		}
	}
	
}
