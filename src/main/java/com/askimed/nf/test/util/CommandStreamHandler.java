package com.askimed.nf.test.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CommandStreamHandler implements Runnable {

	private InputStream is;

	private boolean silent = false;

	private String filename = null;

	public CommandStreamHandler(InputStream is) {
		this.is = is;
	}

	public CommandStreamHandler(InputStream is, String filename) {
		this.is = is;
		this.filename = filename;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public void run() {

		try {

			boolean save = (filename != null && !filename.isEmpty());
			FileOutputStream writer = null;

			byte[] buffer = new byte[200];

			if (save) {
				writer = new FileOutputStream(filename);
			}

			int size = 0;

			while ((size = is.read(buffer)) > 0) {
				if (!silent) {
					String line = new String(buffer, 0, size);
					System.out.println(line);
				}
				if (save) {
					writer.write(buffer, 0, size);
				}
			}

			if (save) {
				writer.close();
			}

			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}