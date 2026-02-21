package com.digitalmoneyhouse.account.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest(
    @Size(max = 100)
    @Pattern(regexp = "^[a-z]+\\.[a-z]+\\.[a-z]+$", message = "alias debe ser 3 palabras en minuscula separadas por punto")
    String alias
) {}
