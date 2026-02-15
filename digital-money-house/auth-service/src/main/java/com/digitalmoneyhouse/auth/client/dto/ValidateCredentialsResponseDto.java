package com.digitalmoneyhouse.auth.client.dto;

public record ValidateCredentialsResponseDto(Long userId, String email, String roleName) {}
