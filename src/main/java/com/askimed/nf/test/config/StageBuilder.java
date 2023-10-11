package com.askimed.nf.test.config;

import java.util.List;
import java.util.Vector;

public class StageBuilder {

	private List<FileStaging> paths = new Vector<FileStaging>();
	
	public void copy(String path) {
		paths.add(new FileStaging(path, FileStaging.MODE_COPY));
	}

	public void symlink(String path) {
		paths.add(new FileStaging(path, FileStaging.MODE_SYMLINK));
	}

	
	public List<FileStaging> getPaths() {
		return paths;
	}
	
}
