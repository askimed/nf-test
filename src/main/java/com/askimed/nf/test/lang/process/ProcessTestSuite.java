package com.askimed.nf.test.lang.process;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.DataDrivenTest;
import com.askimed.nf.test.lang.TestCode;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class ProcessTestSuite extends AbstractTestSuite {

	private String process;

	private TestCode setup;
	
	public void process(String process) {
		setProcess(process);
	}

	public void setProcess(String process) {
		this.process = process;
		tag(process);
	}

	public String getProcess() {
		return process;
	}

	public void test(String name, Closure closure) throws IOException {
		addTestClosure(name, closure);
	}

	public void setup(@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new TestCode(closure);
	}
	
	public TestCode getSetup() {
		return setup;
	}
	
	@Override
	protected ITest getNewTestInstance(String name) {
		ProcessTest test = new ProcessTest(this);
		test.name(name);
		return test;
	}

	public void testEach(String nameTemplate,
			@DelegatesTo(value = DataDrivenTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		final DataDrivenTest dataDrivenTest = new DataDrivenTest(this);
		closure.setDelegate(dataDrivenTest);
		closure.setResolveStrategy(Closure.DELEGATE_ONLY);
		closure.call();

		// Generate individual tests for each data row
		for (int i = 0; i < dataDrivenTest.getDataTable().size(); i++) {
			Map<String, Object> testParams = dataDrivenTest.getDataTable().getRows().get(i);

			// Create test name by replacing placeholders
			String testName = interpolateTestName(nameTemplate, testParams, i);

			// Create parameterized test
			final ProcessTest test = new ProcessTest(this);
			test.name(testName);
			test.setParameters(testParams);

			// Set the test implementation from the data-driven test
			test.setWhenClosure(dataDrivenTest.getWhenClosure());
			test.setThenClosure(dataDrivenTest.getThenClosure());
			test.setSetupClosure(dataDrivenTest.getSetupClosure());
			test.setCleanupClosure(dataDrivenTest.getCleanupClosure());

			addTest(test);
		}
	}

	private String interpolateTestName(String nameTemplate, Map<String, Object> params, int index) {
		String result = nameTemplate;
		
		// Replace parameter placeholders like #paramName with actual values
		for (Map.Entry<String, Object> entry : params.entrySet()) {
			String placeholder = "#" + entry.getKey();
			String value = entry.getValue() != null ? entry.getValue().toString() : "null";
			result = result.replace(placeholder, value);
		}
		
		// Replace #index with the current row index
		result = result.replace("#index", String.valueOf(index));
		
		return result;
	}

}