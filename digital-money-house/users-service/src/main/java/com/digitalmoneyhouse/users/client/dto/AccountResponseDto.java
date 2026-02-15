package com.digitalmoneyhouse.users.client.dto;

import java.math.BigDecimal;

public record AccountResponseDto(Long id, Long userId, String cvu, String alias, BigDecimal balance) {}
