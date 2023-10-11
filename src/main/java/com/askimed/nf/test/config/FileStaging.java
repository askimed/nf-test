package com.askimed.nf.test.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.askimed.nf.test.util.FileUtil;

public class FileStaging {

	public static String MODE_COPY = "copy";

	public static String MODE_SYMLINK = "symlink";

	private String path = "";

	private String mode = MODE_SYMLINK;

	private static Logger log = LoggerFactory.getLogger(FileStaging.class);

	public FileStaging() {

	}

	public FileStaging(String path) {
		this(path, MODE_SYMLINK);
	}

	public FileStaging(String path, String mode) {
		this.path = path;
		this.mode = mode;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getMode() {
		return mode;
	}

	public void stage(String target) throws IOException {

		if (path == null) {
			throw new IOException("No path set.");
		}

		Path localFile = Path.of(path);

		if (localFile.toFile().exists()) {

			String parent = new File(target).getParentFile().getAbsolutePath();
			if (parent != null) {
				FileUtil.createDirectory(parent);
			}

			if (localFile.toFile().isDirectory()) {
				stageDirectory(target, localFile);
			} else {
				stageFile(target, localFile);
			}

		} else {
			log.warn("File '{}' not found. Ignore it.", localFile.toFile().getAbsolutePath());
		}
	}

	private void stageFile(String target, Path localFile) throws IOException {
		if (mode.equalsIgnoreCase(MODE_COPY)) {
			log.info("Copy file '{}' to '{}'", localFile.toFile().getAbsolutePath(), target);
			Files.copy(localFile.toFile().toPath(), Path.of(target));
		} else if (mode.equalsIgnoreCase(MODE_SYMLINK)) {
			log.info("Create symlink '{}' --> '{}'", target, localFile.toFile().getAbsolutePath());
			Files.createSymbolicLink(Path.of(target), localFile.toAbsolutePath());
		}
	}

	private void stageDirectory(String target, Path localFile) throws IOException {
		if (mode.equalsIgnoreCase(MODE_COPY)) {
			log.info("Copy directory '{}' to '{}'", localFile.toFile().getAbsolutePath(), target);
			FileUtil.copyDirectory(localFile.toFile().getAbsolutePath(), target);
		} else if (mode.equalsIgnoreCase(MODE_SYMLINK)) {
			log.info("Create symlink '{}' --> '{}'", target, localFile.toFile().getAbsolutePath());
			Files.createSymbolicLink(Path.of(target), localFile.toAbsolutePath());
		}
	}

}
