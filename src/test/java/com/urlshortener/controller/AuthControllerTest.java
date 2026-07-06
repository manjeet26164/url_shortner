package com.urlshortener.controller;

import com.urlshortener.dto.AuthRequest;
import com.urlshortener.entity.User;
import com.urlshortener.repository.UserRepository;
import com.urlshortener.service.JwtService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Test
    void registerCreatesUserAndReturnsSuccess() {
        UserRepository userRepository = mock(UserRepository.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        AuthController controller = new AuthController(userRepository, authenticationManager, passwordEncoder, jwtService);

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Secret123!")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<?> response = controller.register(new AuthRequest("alice@example.com", "Secret123!"));

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Registration successful", ((com.urlshortener.dto.AuthResponse) response.getBody()).message());
        assertNull(((com.urlshortener.dto.AuthResponse) response.getBody()).token());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerReturnsConflictWhenEmailAlreadyExists() {
        UserRepository userRepository = mock(UserRepository.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        AuthController controller = new AuthController(userRepository, authenticationManager, passwordEncoder, jwtService);

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        ResponseEntity<?> response = controller.register(new AuthRequest("alice@example.com", "Secret123!"));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already registered", ((com.urlshortener.dto.AuthResponse) response.getBody()).message());
        assertNull(((com.urlshortener.dto.AuthResponse) response.getBody()).token());
    }

    @Test
    void loginReturnsJwtToken() {
        UserRepository userRepository = mock(UserRepository.class);
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        JwtService jwtService = mock(JwtService.class);
        AuthController controller = new AuthController(userRepository, authenticationManager, passwordEncoder, jwtService);

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("alice@example.com")
                .password("hashed-password")
                .authorities(java.util.List.of())
                .build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token");

        ResponseEntity<?> response = controller.login(new AuthRequest("alice@example.com", "Secret123!"));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Login successful", ((com.urlshortener.dto.AuthResponse) response.getBody()).message());
        assertEquals("jwt-token", ((com.urlshortener.dto.AuthResponse) response.getBody()).token());
    }
}
