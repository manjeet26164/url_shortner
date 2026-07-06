package com.urlshortener.service;

import com.urlshortener.entity.UrlMapping;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UrlCacheService {

    private static final Duration DEFAULT_TTL = Duration.ofHours(24);
    private static final String KEY_PREFIX = "url:";

    private final RedisTemplate<String, String> redisTemplate;

    public UrlCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Optional<String> get(String shortCode) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key(shortCode)));
    }

    public void put(UrlMapping mapping) {
        put(mapping.getShortCode(), mapping.getLongUrl(), mapping.getExpiresAt());
    }

    public void put(String shortCode, String longUrl, Instant expiresAt) {
        Duration ttl = DEFAULT_TTL;
        if (expiresAt != null) {
            Duration untilExpiry = Duration.between(Instant.now(), expiresAt);
            if (untilExpiry.isZero() || untilExpiry.isNegative()) {
                evict(shortCode);
                return;
            }
            ttl = ttl.compareTo(untilExpiry) < 0 ? ttl : untilExpiry;
        }

        redisTemplate.opsForValue().set(key(shortCode), longUrl, ttl);
    }

    public void evict(String shortCode) {
        redisTemplate.delete(key(shortCode));
    }

    private String key(String shortCode) {
        return KEY_PREFIX + shortCode;
    }
}