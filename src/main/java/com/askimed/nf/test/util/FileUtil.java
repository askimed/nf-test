package com.askimed.nf.test.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import groovy.lang.Writable;
import org.apache.tools.ant.taskdefs.Input;

public class FileUtil {

	public static String path(String... paths) {
		String result = "";
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if (path != null && !path.isEmpty()) {
				if (i > 0 && !path.startsWith(File.separator) && !result.endsWith(File.separator)) {
					if (result.isEmpty()) {
						result += path;
					} else {
						result += File.separator + path;
					}
				} else {
					result += path;
				}
			}
		}
		return result;
	}

	public static boolean createDirectory(String dir) {
		return createDirectory(new File(dir));
	}

	public static boolean createDirectory(File output) {
		if (!output.exists()) {
			return output.mkdirs();
		}
		return true;
	}

	static public boolean deleteDirectory(File path) throws IOException {
		if (path.exists()) {
			File[] files = path.listFiles();
			if (files == null) {
				return false;
			}
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (Files.isSymbolicLink(files[i].toPath())) {
						Files.delete(files[i].toPath());
					} else {
						deleteDirectory(files[i]);
					}
				} else {
					Files.delete(files[i].toPath());
				}
			}
			Files.delete(path.toPath());
			return true;
		}
		return false;
	}

	static public void write(File file, Writable writable) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(writable.toString());
		writer.close();
	}

	public static String readFileAsString(File file) throws IOException {

		StringBuffer fileData = new StringBuffer(1000);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		return fileData.toString();

	}

	public static String makeRelative(File baseDir, File absoluteFile) {
		return baseDir.toURI().relativize(absoluteFile.toURI()).getPath();
	}

	public static String getMd5(Path self) throws IOException, NoSuchAlgorithmException {
		Formatter fm = new Formatter();
		MessageDigest md = MessageDigest.getInstance("MD5");
		// for .gz files, calculate md5 hash on decompressed content
		if (self.toString().endsWith(".gz")) {

			if (!isGzipFile(self.toFile())) {
				throw new RuntimeException("File " + self + " is not a valid gzip file.");
			}

			InputStream is = new FileInputStream(self.toFile());
			GZIPInputStream gzis = new GZIPInputStream(is);
			byte[] buffer = new byte[4096];
			int read = gzis.read(buffer);
			while ( read >= 0) {
				md.update(buffer, 0, read);
				read = gzis.read(buffer);
			}
			is.close();
			gzis.close();
		} else {
			// for other files, calculate md5 hash directly from file
			md.update(Files.readAllBytes(self));
	
		}
		byte[] md5sum = md.digest();
		for (byte b : md5sum) {
			fm.format("%02x", b);
		}
		String result = fm.out().toString();
		fm.close();
		return result;
	}

	private static boolean isGzipFile(File file) {
		try {
			// we need a pushbackstream to look ahead
			PushbackInputStream pb = new PushbackInputStream(new FileInputStream(file), 2);
			byte[] signature = new byte[2];
			pb.read(signature); // read the signature
			pb.unread(signature); // push back the signature to the stream
			// check if matches standard gzip magic number
			pb.close();
			return (signature[0] == (byte) 0x1f && signature[1] == (byte) 0x8b);
		} catch (Exception e) {
			return false;
		}
	}

	public static Path[] list(Path self) {
		File[] files = self.toFile().listFiles();
		Path[] paths = new Path[files.length];
		for (int i = 0; i < files.length; i++) {
			paths[i] = files[i].toPath();
		}
		return paths;
	}

	public static InputStream decompressStream(InputStream input) throws IOException {
		// we need a pushbackstream to look ahead
		PushbackInputStream pb = new PushbackInputStream(input, 2);
		byte[] signature = new byte[2];
		pb.read(signature); // read the signature
		pb.unread(signature); // push back the signature to the stream
		// check if matches standard gzip magic number
		if (signature[0] == (byte) 0x1f && signature[1] == (byte) 0x8b)
			return new GZIPInputStream(pb);
		else
			return pb;
	}

	public static void copyDirectory(String sourceDirectory, String destinationDirectory) throws IOException {
		Path path = Paths.get(sourceDirectory);
		List<Path> files = Files.walk(path).collect(Collectors.toList());
		for (Path source : files) {
			Path destination = Paths.get(destinationDirectory, source.toString().substring(sourceDirectory.length()));
			Files.copy(source, destination);
		}
	}
	
	public static String encodeBase64(Path path) throws IOException {
		byte[] bytes =   readBytesFromFile(path.toFile().getAbsolutePath());
		return encodeBase64(bytes);
	}
	
	public static byte[] readBytesFromFile(String filename) throws IOException {
		FileInputStream in = new FileInputStream(filename);
		byte[] bytes = readBytes(in);
		in.close();
		return bytes;
	}
	
	public static byte[] readBytes(InputStream in) throws IOException {
		byte[] targetArray = new byte[in.available()];
		in.read(targetArray);
		return targetArray;
	}

	public static String encodeBase64(String content) {
		return encodeBase64(content.getBytes());
	}
	
	public static String encodeBase64(byte[] bytes) {
		String encodedContent = java.util.Base64.getEncoder().encodeToString(bytes);
		return encodedContent;
	}

	public static void copy(File source, File dest) throws IOException {
		try (InputStream is = new FileInputStream(source);
			 OutputStream os = new FileOutputStream(dest)) {

			byte[] buffer = new byte[1024];
			int length;

			// Read from the input stream and write to the output stream
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		}
	}
}
