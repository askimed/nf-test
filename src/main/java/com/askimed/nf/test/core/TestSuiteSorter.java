package com.askimed.nf.test.core;

import java.util.Comparator;

public class TestSuiteSorter {

    public static Comparator<ITestSuite> getDefault() {
        return new Comparator<ITestSuite>() {
            @Override
            public int compare(ITestSuite o1, ITestSuite o2) {
                return o1.getFilename().compareTo(o2.getFilename());
            }
        };
    }

    public static Comparator<ITestSuite> getTypeSorter() {
        return new Comparator<ITestSuite>() {
            @Override
            public int compare(ITestSuite o1, ITestSuite o2) {
                return o1.getFilename().compareTo(o2.getFilename());
            }
        };
    }

}
