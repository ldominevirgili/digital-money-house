package com.digitalmoneyhouse.account.dto;

import jakarta.validation.constraints.NotNull;

public record AssociateCardRequest(@NotNull Long cardId) {}
