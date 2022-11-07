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

	public static void assertAll(Closure... closures) throws Exception {
		// Asserts that all supplied closures do not throw exceptions.
		// The number of failed closures is reported in the Exception message
		int failed = 0;

		for (Closure closure : closures) {
			try {
				closure.call();
			}
			catch (Throwable e) {
				failed++;
				System.err.println(e);
			}
		}

		if (failed > 0) {
			throw new Exception(Integer.toString(failed) + " of " + Integer.toString(closures.length) + " assertions failed");
		}
	}
}
