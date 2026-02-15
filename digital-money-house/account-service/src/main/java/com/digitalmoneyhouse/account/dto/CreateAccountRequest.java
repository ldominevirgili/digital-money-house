package com.digitalmoneyhouse.account.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAccountRequest(@NotNull Long userId) {}
