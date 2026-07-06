package com.urlshortener.service;

import com.urlshortener.entity.User;
import com.urlshortener.exception.RateLimitExceededException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RateLimiterService {

    private static final long MAX_URL_CREATIONS_PER_HOUR = 20L;
    private static final Duration WINDOW = Duration.ofHours(1);
    private static final String KEY_PREFIX = "ratelimit:";

    private final RedisTemplate<String, String> redisTemplate;

    public RateLimiterService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void checkCreateLimit(User user) {
        long userId = user.getId();
        Instant windowStart = Instant.now().truncatedTo(ChronoUnit.HOURS);
        String key = key(userId, windowStart);

        Long count = redisTemplate.opsForValue().increment(key);
        if (count == null) {
            throw new IllegalStateException("Unable to update rate limit counter");
        }

        if (count == 1L) {
            redisTemplate.expire(key, WINDOW.toHours(), TimeUnit.HOURS);
        }

        if (count > MAX_URL_CREATIONS_PER_HOUR) {
            long remainingRequests = 0L;
            Instant resetAt = windowStart.plus(WINDOW);
            throw new RateLimitExceededException(
                    "Rate limit exceeded. " + remainingRequests + " requests remain. Limit resets at " + resetAt,
                    remainingRequests,
                    resetAt);
        }
    }

    private String key(long userId, Instant windowStart) {
        return KEY_PREFIX + userId + ":" + windowStart.toEpochMilli();
    }
}