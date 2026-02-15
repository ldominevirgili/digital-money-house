package com.digitalmoneyhouse.account.service;

import com.digitalmoneyhouse.account.dto.AccountResponse;
import com.digitalmoneyhouse.account.dto.BalanceResponse;
import com.digitalmoneyhouse.account.dto.TransactionResponse;
import com.digitalmoneyhouse.account.entity.Account;

import java.util.List;

public interface AccountService {

    AccountResponse createAccountForUser(Long userId);

    BalanceResponse getBalance(Long accountId, Long userId);

    Account getAccountByIdAndUser(Long accountId, Long userId);

    List<TransactionResponse> getTransactions(Long accountId, Long userId);

    AccountSummaryResponse getSummaryByUserId(Long userId);
}
