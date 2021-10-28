package com.askimed.nf.test.lang.extensions;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PathExtension {

	public static String getMd5(Path self) throws IOException, NoSuchAlgorithmException {

		MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(Files.readAllBytes(self));
		byte[] md5sum = md.digest();
		BigInteger bigInt = new BigInteger(1, md5sum);
		return bigInt.toString(16);

	}

}
