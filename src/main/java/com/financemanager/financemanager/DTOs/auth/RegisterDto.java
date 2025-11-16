package com.financemanager.financemanager.DTOs.auth;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;

public record RegisterDto(@NotBlank String username,
        @NotBlank String password,
        Set<String> roles) {
}