package com.askimed.nf.test.lang.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;

import groovy.lang.Closure;

public class PipelineTestSuite extends AbstractTestSuite {

	public PipelineTestSuite() {
		name("main.nf");
	}

	public void test(String name, Closure closure) throws IOException {
		addTestClosure(name, closure);
	}

	@Override
	protected ITest getNewTestInstance(String name) {
		PipelineTest test = new PipelineTest(this);
		test.name(name);
		return test;
	}

	public void testEach(String nameTemplate, Map<String, List<?>> dataTable,
			@DelegatesTo(value = PipelineTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {

		// Extract parameter names and values
		List<String> paramNames = new ArrayList<String>(dataTable.keySet());
		List<List<?>> paramValues = new ArrayList<List<?>>();
		for (String paramName : paramNames) {
			paramValues.add(dataTable.get(paramName));
		}

		// Validate all parameter lists have same length
		if (paramValues.isEmpty()) {
			throw new IllegalArgumentException("Data table cannot be empty");
		}

		int testCount = paramValues.get(0).size();
		for (List<?> values : paramValues) {
			if (values.size() != testCount) {
				throw new IllegalArgumentException("All parameter lists must have the same length");
			}
		}

		// Generate individual tests for each data row
		for (int i = 0; i < testCount; i++) {
			Map<String, Object> testParams = new HashMap<String, Object>();
			for (int j = 0; j < paramNames.size(); j++) {
				testParams.put(paramNames.get(j), paramValues.get(j).get(i));
			}

			// Create test name by replacing placeholders
			String testName = interpolateTestName(nameTemplate, testParams, i);

			// Create parameterized test
			final PipelineTest test = new PipelineTest(this);
			test.name(testName);
			test.setParameters(testParams);

			closure.setDelegate(test);
			closure.setResolveStrategy(Closure.DELEGATE_ONLY);
			closure.call();

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