package com.digitalmoneyhouse.account.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(Long id, Long accountId, BigDecimal amount, String type, String description, Instant createdAt) {}
