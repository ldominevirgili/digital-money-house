package com.digitalmoneyhouse.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CardRequest(
    @NotBlank @Size(max = 20) String number,
    @NotBlank @Size(max = 20) String type,
    @NotBlank @Size(max = 100) String holderName,
    @Size(max = 10) String expiry
) {}
