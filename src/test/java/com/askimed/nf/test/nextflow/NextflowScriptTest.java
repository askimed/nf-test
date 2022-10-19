package com.askimed.nf.test.nextflow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

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
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetProcessNames8() {
		List<String> names = NextflowScript.getProcesseNames("process   \n   PLINK_TO_VCF{ some content }");
		assertEquals(1, names.size());
		assertEquals("PLINK_TO_VCF", names.get(0));
	}

	@Test
	public void testFunctionNames() {
		List<String> names = NextflowScript.getFunctionNames("def function1() { some content }");
		assertEquals(1, names.size());
		assertEquals("function1", names.get(0));
	}

	@Test
	public void testFunctionNames1() {
		List<String> names = NextflowScript.getFunctionNames("def function1\t()\t{ some content }");
		assertEquals(1, names.size());
		assertEquals("function1", names.get(0));
	}

	@Test
	public void testFunctionNames2() {
		List<String> names = NextflowScript.getFunctionNames("\n   def function1()\n{some content }");
		assertEquals(1, names.size());
		assertEquals("function1", names.get(0));
	}

	@Test
	public void testFunctionNames3() {
		List<String> names = NextflowScript.getFunctionNames("def function1(){ some content }");
		assertEquals(1, names.size());
		assertEquals("function1", names.get(0));
	}

	@Test
	public void testFunctionNames4() {
		List<String> names = NextflowScript
				.getFunctionNames("\n   def function1()\n{some content }\\n   def function2(arg1, arg2){some content }\"");
		assertEquals(2, names.size());
		assertEquals("function1", names.get(0));
		assertEquals("function2", names.get(1));
	}

	@Test
	public void testFunctionNames5() {
		List<String> names = NextflowScript.getFunctionNames("DEF function1(arg1,\targ2){ some content }");
		assertEquals(1, names.size());
		assertEquals("function1", names.get(0));
	}

	@Test
	public void testFunctionNames6() {
		List<String> names = NextflowScript.getFunctionNames("def FUNCTION_ONE(){ some content }");
		assertEquals(1, names.size());
		assertEquals("FUNCTION_ONE", names.get(0));
	}

	@Test
	public void testFunctionNames7() {
		List<String> names = NextflowScript.getFunctionNames("\n   def function1(a\n,b\n)   \n    {some content }");
		assertEquals(1, names.size());
		assertEquals("function1", names.get(0));
	}

	@Test
	public void testFunctionNames8() {
		List<String> names = NextflowScript.getFunctionNames("def   \n   FUNCTION1(arg1,arg2){ some content }");
		assertEquals(1, names.size());
		assertEquals("FUNCTION1", names.get(0));
	}

	@Test
	public void testFunctionNames9() {
		List<String> names = NextflowScript.getFunctionNames("def      { some content }");
		assertEquals(0, names.size());
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
		assertEquals(1, names.size());
		assertEquals("process1", names.get(0));
	}

	@Test
	public void testGetWorkflowNames8() {
		List<String> names = NextflowScript.getWorkflowNames("workflow   \n   PLINK_TO_VCF{ some content }");
		assertEquals(1, names.size());
		assertEquals("PLINK_TO_VCF", names.get(0));
	}

	@Test
	public void testGetWorkflowNames9() {
		List<String> names = NextflowScript.getWorkflowNames("workflow      { some content }");
		assertEquals(0, names.size());
	}

	
}
