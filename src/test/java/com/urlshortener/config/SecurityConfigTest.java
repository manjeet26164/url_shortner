package com.urlshortener.config;

import com.urlshortener.service.AppUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    @Test
    void providesPasswordEncoderAndAuthenticationProvider() {
        JwtAuthFilter jwtAuthFilter = mock(JwtAuthFilter.class);
        AppUserDetailsService userDetailsService = mock(AppUserDetailsService.class);
        SecurityConfig securityConfig = new SecurityConfig(jwtAuthFilter, userDetailsService);

        assertInstanceOf(BCryptPasswordEncoder.class, securityConfig.passwordEncoder());
        assertInstanceOf(AuthenticationProvider.class, securityConfig.authenticationProvider());
    }
}
