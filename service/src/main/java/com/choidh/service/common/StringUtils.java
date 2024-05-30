package com.choidh.service.common;

public class StringUtils {
    public static String padLeftUsingFormat(String input, int length, char padChar) {
        return String.format("%1$" + length + "s", input).replace(' ', padChar);
    }

    public static String padRightUsingFormat(String input, int length, char padChar) {
        return String.format("%1$-" + length + "s", input).replace(' ', padChar);
    }
}
