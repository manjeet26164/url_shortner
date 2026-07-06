package com.urlshortener.service;

import com.urlshortener.entity.User;
import com.urlshortener.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AppUserDetailsServiceTest {

    @Test
    void loadUserByUsernameReturnsUserDetails() {
        UserRepository userRepository = mock(UserRepository.class);
        AppUserDetailsService service = new AppUserDetailsService(userRepository);
        User user = new User();
        user.setEmail("alice@example.com");
        user.setPassword("hashed-password");
        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername("alice@example.com");

        assertEquals("alice@example.com", result.getUsername());
        assertEquals("hashed-password", result.getPassword());
    }

    @Test
    void loadUserByUsernameThrowsWhenMissing() {
        UserRepository userRepository = mock(UserRepository.class);
        AppUserDetailsService service = new AppUserDetailsService(userRepository);
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("missing@example.com"));
    }
}
