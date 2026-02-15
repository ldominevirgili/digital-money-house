package com.digitalmoneyhouse.users.client;

import com.digitalmoneyhouse.users.client.dto.AccountResponseDto;
import com.digitalmoneyhouse.users.client.dto.AccountSummaryDto;
import com.digitalmoneyhouse.users.client.dto.CreateAccountRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @PostMapping("/accounts")
    AccountResponseDto createAccount(@RequestBody CreateAccountRequestDto request);

    @GetMapping("/accounts/by-user/{userId}")
    AccountSummaryDto getAccountByUserId(@PathVariable("userId") Long userId);
}
