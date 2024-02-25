package com.askimed.nf.test.core;

import java.util.List;
import java.util.Vector;

public class TestSuiteSharder {

    public TestSuiteSharder(String shard) {

    }

    public static List<ITestSuite> shard(List<ITestSuite> tests, String shard) {
        int[] values = parse(shard);
        return shard(tests, values[0], values[1]);
    }

    public static List<ITestSuite> shard(List<ITestSuite> tests, int i, int n) {
        if (tests == null || tests.isEmpty()) {
            throw new IllegalArgumentException("Input list cannot be null or empty");
        }
        if (i <= 0 || i > n) {
            throw new IllegalArgumentException("i must be greater than 0 and less than or equal to n");
        }

        //count all non skipped tests.
        int totalCases = 0;
        for (ITestSuite testSuite: tests) {
            for (ITest testCase: testSuite.getTests()) {
                if (!testCase.isSkipped()) {
                    totalCases++;
                }
            }
        }
        int chunkSize = (int) Math.ceil((double) totalCases / n);
        int start = (i - 1) * chunkSize;
        int end = Math.min(start + chunkSize, totalCases);

        int index = 0;
        List<ITestSuite> shard = new Vector<ITestSuite>();
        for (ITestSuite testSuite: tests) {
            boolean empty = true;
            for (ITest testCase: testSuite.getTests()) {
                if (!testCase.isSkipped()) {
                    if (index >= start && index < end) {
                        empty = false;
                    } else {
                        testCase.skip();
                    }
                    index++;
                }
            }
            if (!empty) {
                shard.add(testSuite);
            }
        }

        return shard;
    }

    public static List<ITestSuite> shardWithRoundRobin(List<ITestSuite> tests, String shard) {
        int[] values = parse(shard);
        return shardWithRoundRobin(tests, values[0], values[1]);
    }

    public static List<ITestSuite> shardWithRoundRobin(List<ITestSuite> tests, int i, int n) {
        if (tests == null || tests.isEmpty()) {
            throw new IllegalArgumentException("Input list cannot be null or empty");
        }
        if (i <= 0 || i > n) {
            throw new IllegalArgumentException("i must be greater than 0 and less than or equal to n");
        }

        int index = 0;
        List<ITestSuite> shard = new Vector<ITestSuite>();
        for (ITestSuite testSuite: tests) {
            boolean empty = true;
            for (ITest testCase: testSuite.getTests()) {
                if (!testCase.isSkipped()) {
                    if (index % n == (i-1)) {
                        empty = false;
                    } else {
                        testCase.skip();
                    }
                    index++;
                }
            }
            if (!empty) {
                shard.add(testSuite);
            }
        }
        return shard;
    }

    public static int[] parse(String input) throws IllegalArgumentException {
        String[] parts = input.split("/");

        // Check if the input has two parts separated by "/"
        if (parts.length != 2) {
            throw new IllegalArgumentException("Shard format: Input format must be 'i/n'");
        }

        try {
            int i = Integer.parseInt(parts[0]);
            int n = Integer.parseInt(parts[1]);

            // Check if i and n satisfy the conditions
            if (i <= 0 || i > n) {
                throw new IllegalArgumentException("Shard format: i must be greater than 0 and less than or equal to n. Format: i/n");
            }

            return new int[] {i, n};
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Shard format: Invalid format. Both i and n must be integers. Format: i/n");
        }
    }

}
