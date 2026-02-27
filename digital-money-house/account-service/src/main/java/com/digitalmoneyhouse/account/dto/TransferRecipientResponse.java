package com.digitalmoneyhouse.account.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferRecipientResponse(Long accountId, String cvu, String alias, BigDecimal lastAmount, Instant lastAt) {}
