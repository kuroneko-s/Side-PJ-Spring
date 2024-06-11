package com.choidh.service.common;

public class StringUtils {
    public static String padLeftUsingFormat(String input, int length, char padChar) {
        return String.format("%1$" + length + "s", input).replace(' ', padChar);
    }

    public static String padRightUsingFormat(String input, int length, char padChar) {
        return String.format("%1$-" + length + "s", input).replace(' ', padChar);
    }

    public static boolean isNullOrEmpty(String target) {
        return target == null || target.isBlank();
    }

    public static boolean isNotNullAndEmpty(String target) {
        return !isNullOrEmpty(target);
    }
}
