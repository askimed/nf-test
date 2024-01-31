package com.askimed.nf.test.util;

public class Version {

    public static int compare(String version1, String version2) {

        String parts1[] = version1.split("-", 2);
        String parts2[] = version2.split("-", 2);

        String tiles1[] = parts1[0].split("\\.");
        String tiles2[] = parts2[0].split("\\.");

        for (int i = 0; i < tiles1.length; i++) {
            int number1 = Integer.parseInt(tiles1[i].trim());
            int number2 = Integer.parseInt(tiles2[i].trim());

            if (number1 != number2) {

                return number1 > number2 ? 1 : -1;

            }

        }

        if (parts1.length > 1) {
            if (parts2.length > 1) {
                return parts1[1].compareTo(parts2[1]);
            } else {
                return -1;
            }
        } else {
            if (parts2.length > 1) {
                return 1;
            }
        }

        return 0;

    }

}
