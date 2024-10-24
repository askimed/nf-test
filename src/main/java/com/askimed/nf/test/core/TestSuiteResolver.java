package com.askimed.nf.test.core;

import com.askimed.nf.test.lang.TestSuiteBuilder;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

public class TestSuiteResolver {

    private final Environment environment;

    public TestSuiteResolver(Environment environment) {
        this.environment = environment;
    }

    public List<ITestSuite> parse(List<File> scripts) throws Throwable {
        return parse(scripts, new TagQuery());
    }

    public List<ITestSuite> parse(List<File> scripts, TagQuery tagQuery) throws Throwable {

        List<ITestSuite> testSuits = new Vector<ITestSuite>();

        for (File script : scripts) {
            String testId = null;
            Path path = Paths.get(script.getAbsolutePath());
            String fileName = path.getFileName().toString();

            if (fileName.contains("@")) {
                String[] tiles = fileName.split("@");
                String basePath = path.getParent().toString();
                script = new File(Paths.get(basePath, tiles[0]).toString());
                testId = tiles[1];
            }
            if (!script.exists()) {
                throw new Exception("Test file '" + script.getAbsolutePath() + "' not found.");
            }
            ITestSuite testSuite = TestSuiteBuilder.parse(script, environment);

            boolean empty = true;

            for (ITest test : testSuite.getTests()) {
                if (testId != null) {
                    if (!test.getHash().startsWith(testId)) {
                        test.skip();
                    }
                }

                if (!tagQuery.matches(test)) {
                    test.skip();
                }

                if (!test.isSkipped()) {
                    empty = false;
                }

            }

            if (!empty) {
                testSuits.add(testSuite);
            }
        }

        return testSuits;

    }


}
