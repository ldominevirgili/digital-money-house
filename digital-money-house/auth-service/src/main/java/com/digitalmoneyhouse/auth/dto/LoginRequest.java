package com.digitalmoneyhouse.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "email requerido") String email,
    @NotBlank(message = "password requerido") String password
) {}
