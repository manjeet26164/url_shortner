package com.urlshortener.exception;

import java.time.Instant;

public class RateLimitExceededException extends RuntimeException {

    private final long remainingRequests;
    private final Instant resetAt;

    public RateLimitExceededException(String message, long remainingRequests, Instant resetAt) {
        super(message);
        this.remainingRequests = remainingRequests;
        this.resetAt = resetAt;
    }

    public long getRemainingRequests() {
        return remainingRequests;
    }

    public Instant getResetAt() {
        return resetAt;
    }
}