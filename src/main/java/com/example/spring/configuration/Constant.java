package com.example.spring.configuration;

import java.util.regex.Pattern;

/**
 * Application constants.
 */
public class Constant {
    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final int DEFAULT_PAGE_NUM = 0;
    private static Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    private Constant() {
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
}
