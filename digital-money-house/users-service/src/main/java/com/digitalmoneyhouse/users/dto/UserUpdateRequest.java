package com.digitalmoneyhouse.users.dto;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @Size(max = 100) String firstName,
    @Size(max = 100) String lastName
) {}
