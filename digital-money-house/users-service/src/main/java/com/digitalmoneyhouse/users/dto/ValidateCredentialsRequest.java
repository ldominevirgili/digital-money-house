package com.digitalmoneyhouse.users.dto;

import jakarta.validation.constraints.NotBlank;

public record ValidateCredentialsRequest(
    @NotBlank String email,
    @NotBlank String password
) {}
