package com.askimed.nf.test.core;

import java.util.List;

public interface ITaggable {

	public String getName();
	
	public List<String> getTags();
	
	public ITaggable getParent();

}
