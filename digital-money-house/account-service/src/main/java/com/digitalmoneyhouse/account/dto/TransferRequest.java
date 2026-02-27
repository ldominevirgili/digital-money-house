package com.digitalmoneyhouse.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferRequest(
    @NotBlank String target,
    @NotNull @DecimalMin(value = "0.01", message = "monto minimo 0.01") BigDecimal amount,
    String description
) {}
