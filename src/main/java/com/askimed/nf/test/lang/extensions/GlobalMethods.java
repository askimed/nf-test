package com.askimed.nf.test.lang.extensions;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Multiset;
import com.google.common.collect.HashMultiset;

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

	public static void printMultiSet(Multiset<Object> multiset) {
		//display the distinct elements of the multiset with their occurrence count
		System.out.println("MultiSet [");

		for (Multiset.Entry<Object> entry : multiset.entrySet()) {
			System.out.println("Element: " + entry.getElement() + ", Occurrence(s): " + entry.getCount());
		}
		System.out.println("]");

	}

	public static void assertListUnsorted(List<Object> list1, List<Object> list2) throws Exception {
		Multiset<Object> multiSet1 = HashMultiset.create();
		Multiset<Object> multiSet2 = HashMultiset.create();

		for (Object obj : list1){
			multiSet1.add(obj);
		}

		for (Object obj : list2){
			multiSet2.add(obj);
		}

		printMultiSet(multiSet1);
		printMultiSet(multiSet2);

		if (! multiSet1.equals(multiSet2)){
			throw new Exception("Lists not equal: " );
		}

		// return multiSet1;
		
		// if (! new HashSet<>(list1).equals(new HashSet<>(list2))){
		// 	throw new Exception("Lists not equal");
		// }
	}

}
