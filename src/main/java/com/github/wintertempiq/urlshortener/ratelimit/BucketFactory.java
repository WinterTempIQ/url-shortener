package com.github.wintertempiq.urlshortener.ratelimit;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class BucketFactory {
    private static final Bandwidth IP_LIMIT = Bandwidth.builder()
            .capacity(20)
            .refillGreedy(20, Duration.ofMinutes(1))
            .build();

    private static final Bandwidth EMAIL_LIMIT = Bandwidth.builder()
            .capacity(5)
            .refillGreedy(5, Duration.ofMinutes(1))
            .build();

    private static final Bandwidth REDIRECT_LIMIT = Bandwidth.builder()
            .capacity(100)
            .refillGreedy(100, Duration.ofMinutes(1))
            .build();

    private static final Bandwidth REDIRECT_SHORTCODE_LIMIT = Bandwidth.builder()
            .capacity(30)
            .refillGreedy(30, Duration.ofMinutes(1))
            .build();

    private static final Bandwidth CREATE_LINK_LIMIT = Bandwidth.builder()
            .capacity(3)
            .refillGreedy(3, Duration.ofMinutes(1))
            .build();

    public Bucket createBucket(RuleType type) {
        Bandwidth bw = switch (type) {
            case IP -> IP_LIMIT;
            case EMAIL -> EMAIL_LIMIT;
            case REDIRECT -> REDIRECT_LIMIT;
            case REDIRECT_SHORTCODE -> REDIRECT_SHORTCODE_LIMIT;
            case CREATE_LINK -> CREATE_LINK_LIMIT;
        };
        return Bucket.builder().addLimit(bw).build();
    }

}
