package com.askimed.nf.test.core;

import java.io.File;
import java.util.List;
import java.util.Vector;

import com.askimed.nf.test.lang.TestSuiteBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestSuiteResolverTest {

	@Test
	public void executeAllTests() throws Throwable {

		TagQuery query = new TagQuery();
		List<String> tests = collectTests(query);
		Assertions.assertEquals(3, tests.size());
		Assertions.assertTrue(tests.contains("test 1"));
		Assertions.assertTrue(tests.contains("test 2"));
		Assertions.assertTrue(tests.contains("test 3"));

	}

	@Test
	public void executeTestByName() throws Throwable {

		TagQuery query = new TagQuery(List.of("test 1"));
		List<String> tests = collectTests(query);
		Assertions.assertEquals(1, tests.size());
		Assertions.assertTrue(tests.contains("test 1"));

	}

	@Test
	public void executeTestByWrongHash() throws Throwable {
		List<File> scripts = new Vector<File>();
		scripts.add(new File("test-data/suite1.nf.test@aaa"));
		scripts.add(new File("test-data/suite2.nf.test"));
		TestSuiteResolver resolver = new TestSuiteResolver(new Environment());
		List<ITestSuite> testSuits = resolver.parse(scripts);
		List<String> tests = getCollectedTests(testSuits);
		Assertions.assertEquals(1, tests.size());
		Assertions.assertTrue(tests.contains("test 3"));
	}

	@Test
	public void executeTestByHash() throws Throwable {
		ITestSuite testsuite = TestSuiteBuilder.parse(new File("test-data/suite1.nf.test"));
		String hash = testsuite.getTests().get(0).getHash();
		List<File> scripts = new Vector<File>();
		scripts.add(new File("test-data/suite1.nf.test@" + hash));
		scripts.add(new File("test-data/suite2.nf.test"));
		TestSuiteResolver resolver = new TestSuiteResolver(new Environment());
		List<ITestSuite> testSuits = resolver.parse(scripts);
		List<String> tests = getCollectedTests(testSuits);
		Assertions.assertEquals(2, tests.size());
		Assertions.assertTrue(tests.contains("test 1"));
		Assertions.assertTrue(tests.contains("test 3"));
	}

	@Test
	public void executeTestSuiteByName() throws Throwable {
		{
			TagQuery query = new TagQuery(List.of("suite 1"));
			List<String> tests = collectTests(query);
			Assertions.assertEquals(2, tests.size());
			Assertions.assertTrue(tests.contains("test 1"));
			Assertions.assertTrue(tests.contains("test 2"));
		}

		{
			TagQuery query = new TagQuery(List.of("SUITE 1"));
			List<String> tests = collectTests(query);
			Assertions.assertEquals(2, tests.size());
			Assertions.assertTrue(tests.contains("test 1"));
			Assertions.assertTrue(tests.contains("test 2"));
		}
	}

	@Test
	public void executeTestsByTag() throws Throwable {
		{
			TagQuery query = new TagQuery(List.of("tag2"));
			List<String> tests = collectTests(query);
			Assertions.assertEquals(1, tests.size());
			Assertions.assertTrue(tests.contains("test 1"));
		}
		{
			TagQuery query = new TagQuery(List.of("TAG2"));
			List<String> tests = collectTests(query);
			Assertions.assertEquals(1, tests.size());
			Assertions.assertTrue(tests.contains("test 1"));
		}
	}

	@Test
	public void executeTestsByTagAcrossSuites() throws Throwable {

		TagQuery query = new TagQuery(List.of("tag5"));
		List<String> tests = collectTests(query);
		Assertions.assertEquals(2, tests.size());
		Assertions.assertTrue(tests.contains("test 2"));
		Assertions.assertTrue(tests.contains("test 3"));
	}

	@Test
	public void executeTestsBySuiteTag() throws Throwable {

		TagQuery query = new TagQuery(List.of("tag1"));
		List<String> tests = collectTests(query);
		Assertions.assertEquals(2, tests.size());
		Assertions.assertTrue(tests.contains("test 1"));
		Assertions.assertTrue(tests.contains("test 2"));
	}

	@Test
	public void executeTestsByMultipleTags() throws Throwable {

		TagQuery query = new TagQuery(List.of("tag3", "tag4"));
		List<String> tests = collectTests(query);
		Assertions.assertEquals(2, tests.size());
		Assertions.assertTrue(tests.contains("test 1"));
		Assertions.assertTrue(tests.contains("test 2"));
	}

	@Test
	public void excludeTestsByTag() throws Throwable {

		TagQuery query = new TagQuery(List.of(), List.of("tag2"));
		List<String> tests = collectTests(query);
		Assertions.assertEquals(2, tests.size());
		Assertions.assertFalse(tests.contains("test 1"));
		Assertions.assertTrue(tests.contains("test 2"));
		Assertions.assertTrue(tests.contains("test 3"));
	}

	@Test
	public void excludeTestsBySuiteTag() throws Throwable {

		TagQuery query = new TagQuery(List.of(), List.of("tag1"));
		List<String> tests = collectTests(query);
		Assertions.assertEquals(1, tests.size());
		Assertions.assertTrue(tests.contains("test 3"));
	}

	@Test
	public void excludeTestsByName() throws Throwable {

		TagQuery query = new TagQuery(List.of(), List.of("test 1"));
		List<String> tests = collectTests(query);
		Assertions.assertEquals(2, tests.size());
		Assertions.assertFalse(tests.contains("test 1"));
		Assertions.assertTrue(tests.contains("test 2"));
		Assertions.assertTrue(tests.contains("test 3"));
	}

	@Test
	public void includeAndExcludeTags() throws Throwable {

		TagQuery query = new TagQuery(List.of("tag1"), List.of("tag3"));
		List<String> tests = collectTests(query);
		Assertions.assertEquals(1, tests.size());
		Assertions.assertTrue(tests.contains("test 2"));
	}

	protected List<String> collectTests(TagQuery query) throws Throwable {
		List<File> scripts = new Vector<File>();
		scripts.add(new File("test-data/suite1.nf.test"));
		scripts.add(new File("test-data/suite2.nf.test"));
		TestSuiteResolver resolver = new TestSuiteResolver(new Environment());
		List<ITestSuite> testSuits = resolver.parse(scripts, query);
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
