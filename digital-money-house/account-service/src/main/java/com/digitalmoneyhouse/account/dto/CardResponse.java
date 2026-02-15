package com.digitalmoneyhouse.account.dto;

public record CardResponse(Long id, Long accountId, String number, String type, String holderName, String expiry) {}
