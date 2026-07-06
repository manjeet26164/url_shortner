package com.urlshortener.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesUrlNotFound() {
        ProblemDetail problemDetail = handler.handleUrlNotFoundException(new UrlNotFoundException("missing"));
        assertEquals(HttpStatus.NOT_FOUND.value(), problemDetail.getStatus());
        assertEquals("missing", problemDetail.getDetail());
    }

    @Test
    void handlesUrlExpired() {
        ProblemDetail problemDetail = handler.handleUrlExpiredException(new UrlExpiredException("expired"));
        assertEquals(HttpStatus.GONE.value(), problemDetail.getStatus());
        assertEquals("expired", problemDetail.getDetail());
    }

    @Test
    void handlesInvalidAlias() {
        ProblemDetail problemDetail = handler.handleInvalidAliasException(new InvalidAliasException("bad alias"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("bad alias", problemDetail.getDetail());
    }

    @Test
    void handlesIllegalArgument() {
        ProblemDetail problemDetail = handler.handleIllegalArgumentException(new IllegalArgumentException("bad input"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), problemDetail.getStatus());
        assertEquals("bad input", problemDetail.getDetail());
    }

    @Test
    void handlesAuthenticationException() {
        ProblemDetail problemDetail = handler.handleAuthenticationException(new BadCredentialsException("invalid"));
        assertEquals(HttpStatus.UNAUTHORIZED.value(), problemDetail.getStatus());
        assertEquals("invalid", problemDetail.getDetail());
    }

    @Test
    void handlesRateLimitExceeded() {
        RateLimitExceededException exception = new RateLimitExceededException(
                "Rate limit exceeded. 0 requests remain. Limit resets at 2026-07-06T12:00:00Z",
                0L,
                Instant.parse("2026-07-06T12:00:00Z"));

        ProblemDetail problemDetail = handler.handleRateLimitExceededException(exception);

        assertEquals(HttpStatus.TOO_MANY_REQUESTS.value(), problemDetail.getStatus());
        assertEquals(exception.getMessage(), problemDetail.getDetail());
    }
}
