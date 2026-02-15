package com.digitalmoneyhouse.users.dto;

public record UserProfileResponse(Long id, String firstName, String lastName, String email, String cvu, String alias) {}
