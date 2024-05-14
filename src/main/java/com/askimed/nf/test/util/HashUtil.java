package com.askimed.nf.test.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil {

    public static String getMd5(String value) {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte[] md5sum = md.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            return bigInt.toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "??";
        }
    }

}
