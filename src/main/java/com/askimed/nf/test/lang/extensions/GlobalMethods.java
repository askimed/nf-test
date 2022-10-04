package com.askimed.nf.test.lang.extensions;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import groovy.lang.Closure;

public class GlobalMethods {

	public static File file(String filename) {
		return new File(filename);
	}

	public static Path path(String filename) {
		return Paths.get(filename);
	}
	
	public static void with(Object context, Closure closure) {
		closure.setDelegate(context);
		closure.setResolveStrategy(Closure.DELEGATE_FIRST);
		closure.call();
	}
}
