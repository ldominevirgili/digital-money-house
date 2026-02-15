package com.digitalmoneyhouse.account.dto;

import java.math.BigDecimal;

public record AccountResponse(Long id, Long userId, String cvu, String alias, BigDecimal balance) {}
