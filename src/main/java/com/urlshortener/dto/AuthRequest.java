package com.urlshortener.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @Email(message = "email must be valid")
        @NotBlank
        String email,
        @NotBlank
        String password) {
}
