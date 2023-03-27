package com.askimed.nf.test.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.junit.jupiter.api.Test;

public class TestExecutionEngineTest {

	@Test
	public void executeAllTests() throws Exception {

		TagQuery query = new TagQuery();
		List<String> tests = collectTests(query);
		assertEquals(3, tests.size());
		assertTrue(tests.contains("test 1"));
		assertTrue(tests.contains("test 2"));
		assertTrue(tests.contains("test 3"));

	}

	@Test
	public void executeTestByName() throws Exception {

		TagQuery query = new TagQuery("test 1");
		List<String> tests = collectTests(query);
		assertEquals(1, tests.size());
		assertTrue(tests.contains("test 1"));

	}

	@Test
	public void executeTestSuiteByName() throws Exception {
		{
			TagQuery query = new TagQuery("suite 1");
			List<String> tests = collectTests(query);
			assertEquals(2, tests.size());
			assertTrue(tests.contains("test 1"));
			assertTrue(tests.contains("test 2"));
		}

		{
			TagQuery query = new TagQuery("SUITE 1");
			List<String> tests = collectTests(query);
			assertEquals(2, tests.size());
			assertTrue(tests.contains("test 1"));
			assertTrue(tests.contains("test 2"));
		}
	}

	@Test
	public void executeTestsByTag() throws Exception {
		{
			TagQuery query = new TagQuery("tag2");
			List<String> tests = collectTests(query);
			assertEquals(1, tests.size());
			assertTrue(tests.contains("test 1"));
		}
		{
			TagQuery query = new TagQuery("TAG2");
			List<String> tests = collectTests(query);
			assertEquals(1, tests.size());
			assertTrue(tests.contains("test 1"));
		}
	}

	@Test
	public void executeTestsByTagAcrossSuites() throws Exception {

		TagQuery query = new TagQuery("tag5");
		List<String> tests = collectTests(query);
		assertEquals(2, tests.size());
		assertTrue(tests.contains("test 2"));
		assertTrue(tests.contains("test 3"));
	}

	@Test
	public void executeTestsBySuiteTag() throws Exception {

		TagQuery query = new TagQuery("tag1");
		List<String> tests = collectTests(query);
		assertEquals(2, tests.size());
		assertTrue(tests.contains("test 1"));
		assertTrue(tests.contains("test 2"));
	}

	@Test
	public void executeTestsByMultipleTags() throws Exception {

		TagQuery query = new TagQuery("tag3", "tag4");
		List<String> tests = collectTests(query);
		assertEquals(2, tests.size());
		assertTrue(tests.contains("test 1"));
		assertTrue(tests.contains("test 2"));
	}

	protected List<String> collectTests(TagQuery query) throws Exception {
		List<File> scripts = new Vector<File>();
		scripts.add(new File("test-data/suite1.nf.test"));
		scripts.add(new File("test-data/suite2.nf.test"));
		TestExecutionEngine engine = new TestExecutionEngine();
		engine.setScripts(scripts);
		List<ITestSuite> testSuits = engine.parse(query);
		return getCollectedTests(testSuits);
	}

	protected List<String> getCollectedTests(List<ITestSuite> suites) {
		List<String> tests = new Vector<String>();
		for (ITestSuite suite : suites) {
			for (ITest test : suite.getTests()) {
				if (!test.isSkipped()) {
					tests.add(test.getName());
				}
			}
		}
		return tests;
	}

}
