package com.github.wintertempiq.urlshortener.ratelimit;

import java.time.Duration;
import java.time.Instant;

public class RateLimitEntry {

    private int count;
    private Instant windowStart = Instant.now();

    synchronized boolean tryConsume() {
        Instant now = Instant.now();
        if (Duration.between(windowStart, now).compareTo(Duration.ofMinutes(1)) >= 0) {
            windowStart = Instant.now();
            count = 0;
        }

        if (count >= 5) {
            return false;
        }

        count++;
        return true;
    }
}
