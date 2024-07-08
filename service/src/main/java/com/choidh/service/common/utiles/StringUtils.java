package com.choidh.service.common.utiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class StringUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    public static String translationListToString(List<String> list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
