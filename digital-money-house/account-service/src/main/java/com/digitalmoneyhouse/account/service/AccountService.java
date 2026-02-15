package com.digitalmoneyhouse.account.service;

import com.digitalmoneyhouse.account.dto.AccountResponse;

public interface AccountService {

    AccountResponse createAccountForUser(Long userId);
}
