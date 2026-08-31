package com.github.wintertempiq.urlshortener.ratelimit;

import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class RateLimiter {
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final BucketFactory bucketFactory;

    public boolean isAllowed(RateLimitKey... keys) {
        for (RateLimitKey key : keys) {
            Bucket bucket = buckets.computeIfAbsent(
                    key.key(),
                    k -> bucketFactory.createBucket(key.type())
            );
            if (!bucket.tryConsume(1)){
                return false;
            }
        }
        return true;
    }
}
