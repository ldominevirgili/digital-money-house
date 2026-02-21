package com.digitalmoneyhouse.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record DepositRequest(
    @NotNull Long cardId,
    @NotNull @DecimalMin(value = "0.01", message = "monto minimo 0.01") BigDecimal amount,
    String description
) {}
