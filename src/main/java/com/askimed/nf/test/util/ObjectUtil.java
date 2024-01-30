package com.askimed.nf.test.util;

import com.askimed.nf.test.lang.extensions.SnapshotFile;
import groovy.json.JsonGenerator;
import groovy.json.JsonOutput;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ObjectUtil {

    public static String getMd5(Object object) {
        JsonGenerator jsonGenerator = SnapshotFile.createJsonGenerator();
        String json = jsonGenerator.toJson(object);
        try {
            return calculateMD5(JsonOutput.prettyPrint(json));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public static String calculateMD5(String input) throws NoSuchAlgorithmException {
        // Get an instance of the MD5 message digest algorithm
        MessageDigest md = MessageDigest.getInstance("MD5");

        // Update the digest with the input string's bytes
        md.update(input.getBytes());

        // Get the hash value as an array of bytes
        byte[] digest = md.digest();

        // Convert the byte array to a hexadecimal string
        StringBuilder result = new StringBuilder();
        for (byte b : digest) {
            result.append(String.format("%02x", b));
        }

        return result.toString();
    }
}
