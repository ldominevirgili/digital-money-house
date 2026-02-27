package com.digitalmoneyhouse.account.dto;

import jakarta.validation.constraints.Size;

public record CardUpdateRequest(
    @Size(max = 100) String holderName,
    @Size(max = 10) String expiry
) {}
