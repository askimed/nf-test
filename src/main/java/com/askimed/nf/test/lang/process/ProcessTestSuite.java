package com.askimed.nf.test.lang.process;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.core.AbstractTestSuite;
import com.askimed.nf.test.core.ITest;
import com.askimed.nf.test.lang.TestCode;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

/**
* ProcessTestSuite is an implementation of AbstractTestSuite that allows testing for nextflow processes.
* It provides a DSL to define the process to be tested, the setup code and the test cases.
* 
*/
public class ProcessTestSuite extends AbstractTestSuite {

	/**
	 * The process to be tested. This is the name of the process as defined in the nextflow script.
	 */
	private String process;

	/**
	 * The setup code to be executed before each test case.
	 */
	private TestCode setup;

	/**
	 * The list of topics channel names to be checked for in the test suite.
	 */
	private Vector<String> topics = new Vector<String>();
	
	/**
	 * Set a process name. Alias of {@link #setProcess(String)}.
	 * @param process The name of the process to be tested.
	 */
	public void process(String process) {
		setProcess(process);
	}

	/**
	 * Set a process name. Alias of {@link #process(String)}.
	 * @param process The name of the process to be tested.
	 */

	public void setProcess(String process) {
		this.process = process;
		tag(process);
	}

	/**
	 * Get the process name.
	 * @return The name of the process to be tested.
	 */
	public String getProcess() {
		return process;
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

	/**
	 * Add a test case to the test suite.
	 * @param name The name of the test case.
	 * @param closure The closure containing the test code.
	 * @throws IOException If an error occurs while adding the test case.
	 */
	public void test(String name, Closure closure) throws IOException {
		addTestClosure(name, closure);
	}

	/**
	 * Set the setup code to be executed before each test case.
	 * @param closure The closure containing the setup code.
	 */
	public void setup(@DelegatesTo(value = ProcessTest.class, strategy = Closure.DELEGATE_ONLY) final Closure closure) {
		setup = new TestCode(closure);
	}
	
	/**
	 * Get the setup code to be executed before each test case.
	 * @return The setup code to be executed before each test case.
	 */
	public TestCode getSetup() {
		return setup;
	}
	
	/**
	 * Create a new test instance.
	 * @param name The name of the test case.
	 * @return A new test instance.
	 */
	@Override
	protected ITest getNewTestInstance(String name) {
		ProcessTest test = new ProcessTest(this);
		test.name(name);
		return test;
	}

}