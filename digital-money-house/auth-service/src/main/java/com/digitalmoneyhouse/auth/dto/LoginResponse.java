package com.digitalmoneyhouse.auth.dto;

public record LoginResponse(String token, String type, Long userId, String email) {

    public static LoginResponse of(String token, Long userId, String email) {
        return new LoginResponse(token, "Bearer", userId, email);
    }
}
