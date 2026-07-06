package com.urlshortener.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String JWT_SECRET = "01234567890123456789012345678901";

    @Test
    void generateTokenExtractsAndValidatesUsername() {
        JwtService jwtService = new JwtService(JWT_SECRET);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("alice@example.com")
                .password("hashed-password")
                .authorities(List.of())
                .build();

        String token = jwtService.generateToken(userDetails);

        assertEquals("alice@example.com", jwtService.extractUsername(token));
        assertTrue(jwtService.validateToken(token));
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void tokenValidationFailsForDifferentUser() {
        JwtService jwtService = new JwtService(JWT_SECRET);
        UserDetails alice = org.springframework.security.core.userdetails.User
                .withUsername("alice@example.com")
                .password("hashed-password")
                .authorities(List.of())
                .build();
        UserDetails bob = org.springframework.security.core.userdetails.User
                .withUsername("bob@example.com")
                .password("hashed-password")
                .authorities(List.of())
                .build();

        String token = jwtService.generateToken(alice);

        assertFalse(jwtService.isTokenValid(token, bob));
    }

    @Test
    void constructorRejectsBlankSecret() {
        assertThrows(IllegalStateException.class, () -> new JwtService(""));
    }
}
