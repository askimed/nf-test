package com.askimed.nf.test.core;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import com.askimed.nf.test.config.Config;

class AbstractTestDevResumeTest {

    class DummySuite extends AbstractTestSuite {

        public DummySuite() {
            super(); // use correct constructor
        }

        @Override
        public AbstractTest getNewTestInstance(String name) {
            return new DummyTest(this);
        }
    }

    class DummyTest extends AbstractTest {

        public DummyTest(AbstractTestSuite parent) {
            super(parent);
        }

        @Override
        public String getName() {
            return "dummy-test";
        }

        @Override
        public String getHash() {
            return "dummy-hash"; // bypass real hash generation
        }
    }

    @Test
    void workDirectoryIsDeletedInNormalMode() throws Exception {

        File tempDir = Files.createTempDirectory("nf-test").toFile();

        DummySuite suite = new DummySuite();
        DummyTest test = new DummyTest(suite);

        test.defineDirectories(tempDir);

        // create a file in work directory
        File testFile = new File(test.workDir, "marker.txt");
        test.workDir.mkdirs();
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("hello");
        }

        assertTrue(testFile.exists());

        test.setDevResume(false);
        test.setup(new Config());

        // file should be gone
        assertFalse(testFile.exists());
    }
}
