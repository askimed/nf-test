package com.askimed.nf.test.nextflow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.askimed.nf.test.nextflow.NextflowScript;

public class NextflowScriptTest {

	@Test
	public void testGetProcessNames() {
		List<String> names = NextflowScript.getProcesseNames("process process1 { some content }");
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetProcessName1() {
		List<String> names = NextflowScript.getProcesseNames("process process1\t{some content }");
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetProcessName2() {
		List<String> names = NextflowScript.getProcesseNames("\n   process process1\n{some content }");
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetProcessNames3() {
		List<String> names = NextflowScript.getProcesseNames("process process1{ some content }");
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetProcessName4() {
		List<String> names = NextflowScript
				.getProcesseNames("\n   process process1\n{some content }\\n   process process2{some content }\"");
		assertEquals(2, names.size());
		assertEquals("process1", names.get(0));
		assertEquals("process2", names.get(1));
	}

	@Test
	public void testGetProcessNames5() {
		List<String> names = NextflowScript.getProcesseNames("PROCESS process1{ some content }");
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetProcessNames6() {
		List<String> names = NextflowScript.getProcesseNames("process PLINK_TO_VCF{ some content }");
		assertEquals(1, names.size());
		assertEquals("PLINK_TO_VCF", names.get(0));
	}

	@Test
	public void testGetProcessName7() {
		List<String> names = NextflowScript.getProcesseNames("\n   process process1   \n    {some content }");
		// TODO: fix parser
		// assertEquals(1, names.size());
		// assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetProcessNames8() {
		List<String> names = NextflowScript.getProcesseNames("process   \n   PLINK_TO_VCF{ some content }");
		// TODO: fix parser
		// assertEquals(1, names.size());
		// assertEquals("PLINK_TO_VCF", names.get(0));
	}

	@Test
	public void testGetWorkflowNames() {
		List<String> names = NextflowScript.getWorkflowNames("workflow process1 { some content }");
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetWorkflowName1() {
		List<String> names = NextflowScript.getWorkflowNames("workflow process1\t{some content }");
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetWorkflowName2() {
		List<String> names = NextflowScript.getWorkflowNames("\n   workflow process1\n{some content }");
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetWorkflowNames3() {
		List<String> names = NextflowScript.getWorkflowNames("workflow process1{ some content }");
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetWorkflowName4() {
		List<String> names = NextflowScript
				.getWorkflowNames("\n   workflow process1\n{some content }\\n   workflow process2{some content }\"");
		assertEquals(2, names.size());
		assertEquals("process1", names.get(0));
		assertEquals("process2", names.get(1));
	}

	@Test
	public void testGetWorkflowsNames5() {
		List<String> names = NextflowScript.getWorkflowNames("WORKFLOW process1{ some content }");
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetWorkflowNames6() {
		List<String> names = NextflowScript.getWorkflowNames("workflow PLINK_TO_VCF{ some content }");
		assertEquals(1, names.size());
		assertEquals("PLINK_TO_VCF", names.get(0));
	}

	@Test
	public void testGetWorkflowName7() {
		List<String> names = NextflowScript.getWorkflowNames("\n   workflow process1   \n    {some content }");
		// TODO: fix parser
		// assertEquals(1, names.size());
		// assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetWorkflowNames8() {
		List<String> names = NextflowScript.getWorkflowNames("workflow   \n   PLINK_TO_VCF{ some content }");
		// TODO: fix parser
		// assertEquals(1, names.size());
		// assertEquals("PLINK_TO_VCF", names.get(0));
	}

	@Test
	public void testGetWorkflowNames9() {
		List<String> names = NextflowScript.getWorkflowNames("workflow      { some content }");
		assertEquals(0, names.size());
	}

}
