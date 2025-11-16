package com.financemanager.financemanager.DTOs.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(@NotBlank String username, @NotBlank String password) {}