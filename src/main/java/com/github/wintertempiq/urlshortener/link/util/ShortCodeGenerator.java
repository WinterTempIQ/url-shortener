package com.github.wintertempiq.urlshortener.link.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {
    private static final char[] BASE62 =
            "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private static final SecureRandom RANDOM = new SecureRandom();

    public String getShortCode() {
        return getShortCode(6);
    }

    public String getShortCode(int length) {

        StringBuilder shortCode = new StringBuilder();

        for (int i = 0; i < length; i++) {
            shortCode = shortCode.append(BASE62[RANDOM.nextInt(BASE62.length)]);
        }

        return shortCode.toString();
    }

}
