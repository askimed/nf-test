package com.github.lukfor.nf.test.nextflow;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.lukfor.nf.test.nextflow.NextflowScript;

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


}
