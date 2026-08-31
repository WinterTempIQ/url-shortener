package com.github.wintertempiq.urlshortener.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RateLimiter {
    private final BucketFactory bucketFactory;

    private final Cache<String, Bucket> buckets = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofMinutes(15))
            .maximumSize(10_000)
            .build();

    public boolean isAllowed(RateLimitKey... keys) {
        for (RateLimitKey key : keys) {
            Bucket bucket = buckets.get(
                    key.key(),
                    k -> bucketFactory.createBucket(key.type())
            );
            if (!bucket.tryConsume(1)) {
                return false;
            }
        }
        return true;
    }
}
