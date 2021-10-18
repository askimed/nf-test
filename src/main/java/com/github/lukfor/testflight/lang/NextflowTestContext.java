package com.github.lukfor.testflight.lang;

import java.util.HashMap;
import java.util.Map;

public class NextflowTestContext {

	private Map<String, Object> params = new HashMap<String, Object>();

	private Workflow workflow = new Workflow();

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public static class Workflow {

		public boolean success = true;
		
		public int exitCode = 0;
		
		public boolean failed = false;
		
	}

}
