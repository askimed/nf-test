package com.askimed.nf.test.util;

import com.askimed.nf.test.lang.extensions.SnapshotFile;
import com.askimed.nf.test.lang.extensions.util.PathConverter;
import groovy.json.JsonGenerator;
import groovy.json.JsonOutput;
import groovy.json.JsonSlurper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ObjectUtil {

    public static String getMd5(Object object) {
        JsonGenerator jsonGenerator = createJsonGenerator();
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


    public static String toJson(Object object) {
        JsonGenerator jsonGenerator = createJsonGenerator();
        String json = jsonGenerator.toJson(object);
        return JsonOutput.prettyPrint(json);
    }

    public static String toJsonRaw(Object object) {
        JsonGenerator jsonGenerator = createJsonGeneratorRaw();
        String json = jsonGenerator.toJson(object);
        return JsonOutput.prettyPrint(json);
    }

    public static JsonGenerator createJsonGenerator() {
        return new JsonGenerator.Options().excludeFieldsByName("mapping")
                .addConverter(new PathConverter()).build();
    }

    public static JsonGenerator createJsonGeneratorRaw() {
        return new JsonGenerator.Options().excludeFieldsByName("mapping").build();
    }


    public static Object toMap(Object object) {
        JsonGenerator jsonGenerator = createJsonGeneratorRaw();
        String json = jsonGenerator.toJson(object);
        return new JsonSlurper().parseText(json);
    }

}
