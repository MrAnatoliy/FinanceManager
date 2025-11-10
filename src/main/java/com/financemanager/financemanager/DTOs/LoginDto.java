package com.financemanager.financemanager.DTOs;

import jakarta.validation.constraints.NotBlank;

public record LoginDto(@NotBlank String username, @NotBlank String password) {}