package com.askimed.nf.test.core;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Test;

import com.askimed.nf.test.config.Config;

class AbstractTestTest {

	class DummySuite extends AbstractTestSuite {

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
			return "dummy-hash";
		}
	}

	private File createMarker(File directory) throws IOException {
		directory.mkdirs();
		File marker = new File(directory, "marker.txt");
		Files.writeString(marker.toPath(), "hello");
		return marker;
	}

	private DummyTest createTest(boolean devResume) throws IOException {
		DummyTest test = new DummyTest(new DummySuite());
		test.defineDirectories(Files.createTempDirectory("nf-test").toFile());
		test.setDevResume(devResume);
		return test;
	}

	@Test
	void setupDeletesAllDirectories() throws Exception {

		DummyTest test = createTest(false);
		File launchMarker = createMarker(test.launchDir);
		File workMarker = createMarker(test.workDir);

		test.setup(new Config());

		assertFalse(launchMarker.exists());
		assertFalse(workMarker.exists());
	}

	@Test
	void setupWithDevResumeKeepsLaunchAndWorkDirectories() throws Exception {

		DummyTest test = createTest(true);
		File launchMarker = createMarker(test.launchDir);
		File workMarker = createMarker(test.workDir);

		test.setup(new Config());

		assertTrue(launchMarker.exists());
		assertTrue(workMarker.exists());
	}

	@Test
	void setupWithDevResumeClearsOutputAndMetaDirectories() throws Exception {

		DummyTest test = createTest(true);
		File outputMarker = createMarker(test.outputDir);
		File metaMarker = createMarker(test.metaDir);

		test.setup(new Config());

		assertFalse(outputMarker.exists());
		assertFalse(metaMarker.exists());
		assertTrue(test.outputDir.exists());
		assertTrue(test.metaDir.exists());
	}

}
