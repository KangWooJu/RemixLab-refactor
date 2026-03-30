package org.woojukang.remixlab.global.utils.app;

import org.springframework.stereotype.Component;

import java.util.Optional;


public class AppUtils {

    private static final int DEFAULT_MAX_LENGTH = 40;

    public static String truncateOrDefault(String input) {
        return truncateOrDefault(input, DEFAULT_MAX_LENGTH);
    }

    // 문자열이 40자를 넘어가면 자르는 메소드
    public static String truncateOrDefault(String input, int maxLength) {
        return Optional.ofNullable(input)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.length() <= maxLength ? s : s.substring(0, maxLength - 3) + "...")
                .orElse("Untitled");
    }

}
