package com.github.wintertempiq.urlshortener.ratelimit;

public record RateLimitKey(String key, RuleType type) {
}
