package com.askimed.nf.test.lang.extensions;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import com.askimed.nf.test.util.ObjectUtil;
import groovy.lang.Closure;
import junit.framework.AssertionFailedError;

import org.codehaus.groovy.runtime.powerassert.PowerAssertionError;

import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;

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

	public static void assertAll(Closure... closures) throws PowerAssertionError {
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
			throw new PowerAssertionError(Integer.toString(failed) + " of " + Integer.toString(closures.length) + " assertions failed");
		}
	}
	
	public static void assertContainsInAnyOrder(List<Object> list, List<Object> expected) throws Exception {
		// Asserts two order-agnostic lists are equal.
		// Supports sublists, maps, JsonSlurpers objects and anything else with an appropriate equals method.
		try {
			assertThat(list, Matchers.containsInAnyOrder(expected.toArray()));
		}
		catch (Throwable thrown) {
			throw new PowerAssertionError(thrown.getMessage());
		}
	}

	public static String format(String format, Number number) {
		DecimalFormat df = new DecimalFormat(format, DecimalFormatSymbols.getInstance(Locale.US));
		return df.format(number);
	}

	public static String md5(Object object) {
		return ObjectUtil.getMd5(object);
	}
}
