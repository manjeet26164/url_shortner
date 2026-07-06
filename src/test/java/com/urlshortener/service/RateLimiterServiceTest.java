package com.urlshortener.service;

import com.urlshortener.entity.User;
import com.urlshortener.exception.RateLimitExceededException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RateLimiterServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Test
    void allowsRequestsWithinLimitAndSetsExpiry() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        RateLimiterService rateLimiterService = new RateLimiterService(redisTemplate);
        rateLimiterService.checkCreateLimit(user(42L));

        verify(redisTemplate).expire(anyString(), anyLong(), org.mockito.ArgumentMatchers.eq(TimeUnit.HOURS));
    }

    @Test
    void throwsWhenLimitExceeded() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(21L);

        RateLimiterService rateLimiterService = new RateLimiterService(redisTemplate);

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () -> rateLimiterService.checkCreateLimit(user(42L)));

        assertEquals(0L, exception.getRemainingRequests());
        assertEquals(Instant.now().truncatedTo(ChronoUnit.HOURS).plus(1, ChronoUnit.HOURS), exception.getResetAt());
    }

    private User user(long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}