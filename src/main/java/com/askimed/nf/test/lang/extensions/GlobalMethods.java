package com.askimed.nf.test.lang.extensions;

// import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

// import org.junit.jupiter.api.Assertions.*;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

import groovy.json.JsonSlurper;
import groovy.lang.Closure;
import junit.framework.AssertionFailedError;

// import groovy.test.GroovyAssert;

import org.codehaus.groovy.runtime.powerassert.PowerAssertionError;
// import org.hamcrest.Matchers;

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

	public static void assertAll(Closure... closures) throws AssertionFailedError {
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

	public static void assertListUnsorted(List<Object> list1, List<Object> list2) throws AssertionFailedError {
		// Asserts that the two lists
		// The number of failed closures is reported in the Exception message
		
		Multiset<Object> multiSet1 = HashMultiset.create();
		Multiset<Object> multiSet2 = HashMultiset.create();

		for (Object obj : list1) {
			multiSet1.add(obj);
		}

		for (Object obj : list2) {
			multiSet2.add(obj);
		}

		if (!(multiSet1.equals(multiSet2))) {
			throw new PowerAssertionError("Lists not equal.\n List 1: " + multiSet1.toString() + "\n List 2: " + multiSet2.toString());
		}
	}

	public static void assertChannelOutput(List<Object> list1, List<Object> list2) throws AssertionFailedError {
		// Assert that a channel contains the elements provided in the list.
		// Filepaths in the channel are converted to their basename prior to comparison
		List<Object> listWithBasename1 = replaceAbsolutePathsWithBasename(list1);
		List<Object> listWithBasename2 = replaceAbsolutePathsWithBasename(list2);
		
		assertListUnsorted(listWithBasename1, listWithBasename2);
	}

	public static List<Object> replaceAbsolutePathsWithBasename(List<Object> list){
		List<Object> newList = new ArrayList<Object>();

		for (Object item : list) {
			if (item instanceof String & item.toString().startsWith("/")) {
				newList.add(new File((String) item).getName());
			}
			else if (item instanceof List<?>){
				List<Object> listItem = (List<Object>) item;
				List<Object> newListItem = new ArrayList<>();

				for (Object subItem : listItem) {
					if (subItem instanceof String & subItem.toString().startsWith("/")) {
						newListItem.add(new File((String) subItem).getName());
					}
					else{
						newListItem.add(subItem);
					}				
				}

				newList.add(newListItem);
			}
			else{
				newList.add(item);
			}
		}

		return newList;
	}

	public static Object parseIfKnownPathExtension(Object obj) throws Exception {
		//
		Object parsed = obj;

		if (obj instanceof String) {
			String string = (String) obj;

			//  TODO: generalise to all PathExtensions...
			if (string.endsWith(".json")) {
				parsed = PathExtension.getJson(Paths.get(string));
			}
			else if (string.startsWith("/")){
				parsed = Paths.get(string).getFileName().toString();
			}
		}

		return parsed;
	}
	
	public static void assertContainsInAnyOrder(List<Object> list, List<Object> expected) throws Exception {
		try {
			assertThat(list, Matchers.containsInAnyOrder(expected.toArray()));
		}
		catch (Throwable thrown) {
			throw new PowerAssertionError(thrown.getMessage());
		}
		// assertThat(list, Matchers.contains(expected));
		
		
	}

	public static void assertContains(List<Object> list, Object expected) throws Exception {

	} 
}
