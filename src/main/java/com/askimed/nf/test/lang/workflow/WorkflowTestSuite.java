package com.askimed.nf.test.lang.workflow;

import java.io.IOException;
import java.util.Vector;

import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.TestCode;
import com.askimed.nf.test.lang.process.ProcessTest;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

public class WorkflowTestSuite extends AbstractTestSuite {

	private String workflow;

	private TestCode setup;

	/**
	 * The list of topics channel names to be checked for in the test suite.
	 */
	private Vector<String> topics = new Vector<String>();

	public void workflow(String workflow) {
		setWorkflow(workflow);
	}

	public void setWorkflow(String workflow) {
		this.workflow = workflow;
		tag(workflow);
	}

	public String getWorkflow() {
		return workflow;
	}

	/**
	 * Set a list of topics channel names to be checked for in the test suite. Alias of {@link #setTopics(String...)}.
	 * @param topics A list of topics channel names to be checked for in the test suite.
	 */

	public void topics(String... topics) {
		setTopics(topics);
	}

	/**
	 * Set a list of topics channel names to be checked for in the test suite. Alias of {@link #topics(String...)}.
	 * @param topics A list of topics channel names to be checked for in the test suite.
	 */
	public void setTopics(String... topics) {
		for (String topic : topics) {
			if (!this.topics.contains(topic)) {
				this.topics.add(topic);
			}
		}
	}

	/**
	 * Get a list of topics channel names to be checked for in the test suite.
	 * @return A list of topics channel names to be checked for in the test suite.
	 */
	public Vector<String> getTopics() {
		return topics;
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
		WorkflowTest test = new WorkflowTest(this);
		test.name(name);
		return test;
	}

}