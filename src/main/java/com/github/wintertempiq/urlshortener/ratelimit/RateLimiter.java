package com.github.wintertempiq.urlshortener.ratelimit;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimiter {

    private final ConcurrentHashMap<String, RateLimitEntry> counters = new ConcurrentHashMap<>();

    public boolean isAllowed(String key) {

        RateLimitEntry counter = counters.computeIfAbsent(key, k -> new RateLimitEntry());

        return counter.tryConsume();
    }
}
