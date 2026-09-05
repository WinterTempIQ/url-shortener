package com.github.wintertempiq.urlshortener.ratelimit;

import io.github.bucket4j.Bucket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RateLimiterTest {

    @Mock
    private BucketFactory bucketFactory;

    @InjectMocks
    private RateLimiter rateLimiter;

    @Test
    void shouldReturnTrueWhenBucketAllowsRequest() {
        Bucket bucket = mock(Bucket.class);
        when(bucketFactory.createBucket(any(RuleType.class))).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);

        RateLimitKey key = new RateLimitKey("ip:1.2.3.4", RuleType.IP);

        boolean allowed = rateLimiter.isAllowed(key);

        assertTrue(allowed);
    }

    @Test
    void shouldReturnFalseWhenBucketRejectsRequest() {
        Bucket bucket = mock(Bucket.class);
        when(bucketFactory.createBucket(any(RuleType.class))).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(false);

        RateLimitKey key = new RateLimitKey("ip:1.2.3.4", RuleType.IP);

        boolean allowed = rateLimiter.isAllowed(key);

        assertFalse(allowed);
    }

    @Test
    void shouldReturnFalseWhenAnyKeyRejectsEvenIfFirstAllows() {
        Bucket allowBucket = mock(Bucket.class);
        Bucket rejectBucket = mock(Bucket.class);

        when(bucketFactory.createBucket(RuleType.IP)).thenReturn(allowBucket);
        when(allowBucket.tryConsume(1)).thenReturn(true);

        when(bucketFactory.createBucket(RuleType.EMAIL)).thenReturn(rejectBucket);
        when(rejectBucket.tryConsume(1)).thenReturn(false);

        RateLimitKey ipKey = new RateLimitKey("ip:1.2.3.4", RuleType.IP);
        RateLimitKey emailKey = new RateLimitKey("email:test@mail.com", RuleType.EMAIL);

        boolean allowed = rateLimiter.isAllowed(ipKey, emailKey);

        assertFalse(allowed);
    }

    @Test
    void shouldReuseBucketForSameKey() {
        Bucket bucket = mock(Bucket.class);

        when(bucketFactory.createBucket(any(RuleType.class))).thenReturn(bucket);
        when(bucket.tryConsume(1)).thenReturn(true);

        RateLimitKey key = new RateLimitKey("ip:1.2.3.4", RuleType.IP);

        rateLimiter.isAllowed(key);
        rateLimiter.isAllowed(key);
        rateLimiter.isAllowed(key);

        verify(bucketFactory, times(1)).createBucket(RuleType.IP);
        verify(bucket, times(3)).tryConsume(1);
    }
}