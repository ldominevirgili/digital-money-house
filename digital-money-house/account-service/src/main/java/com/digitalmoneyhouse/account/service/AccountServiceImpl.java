package com.digitalmoneyhouse.account.service;

import com.digitalmoneyhouse.account.dto.AccountResponse;
import com.digitalmoneyhouse.account.dto.BalanceResponse;
import com.digitalmoneyhouse.account.dto.TransactionResponse;
import com.digitalmoneyhouse.account.entity.Account;
import com.digitalmoneyhouse.account.entity.Transaction;
import com.digitalmoneyhouse.account.exception.ForbiddenException;
import com.digitalmoneyhouse.account.exception.ResourceNotFoundException;
import com.digitalmoneyhouse.account.repository.AccountRepository;
import com.digitalmoneyhouse.account.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CvuAliasGenerator cvuAliasGenerator;

    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository,
                             CvuAliasGenerator cvuAliasGenerator) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.cvuAliasGenerator = cvuAliasGenerator;
    }

    @Override
    @Transactional
    public AccountResponse createAccountForUser(Long userId) {
        if (accountRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("Ya existe una cuenta para el usuario " + userId);
        }
        CvuAliasGenerator.CvuAliasUniquenessChecker checker = new CvuAliasGenerator.CvuAliasUniquenessChecker() {
            @Override
            public boolean existsCvu(String cvu) { return accountRepository.existsByCvu(cvu); }
            @Override
            public boolean existsAlias(String alias) { return accountRepository.existsByAlias(alias); }
        };
        String cvu = cvuAliasGenerator.generateUniqueCvu(checker);
        String alias = cvuAliasGenerator.generateUniqueAlias(checker);
        Account account = new Account(userId, cvu, alias);
        account.setBalance(BigDecimal.ZERO);
        account = accountRepository.save(account);
        return new AccountResponse(account.getId(), account.getUserId(), account.getCvu(), account.getAlias(), account.getBalance());
    }

    @Override
    public BalanceResponse getBalance(Long accountId, Long userId) {
        Account account = getAccountByIdAndUser(accountId, userId);
        return new BalanceResponse(account.getBalance());
    }

    @Override
    public Account getAccountByIdAndUser(Long accountId, Long userId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
        if (!account.getUserId().equals(userId)) {
            throw new ForbiddenException("Sin permisos");
        }
        return account;
    }

    @Override
    public List<TransactionResponse> getTransactions(Long accountId, Long userId) {
        getAccountByIdAndUser(accountId, userId);
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(t -> new TransactionResponse(t.getId(), t.getAccountId(), t.getAmount(), t.getType(), t.getDescription(), t.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Override
    public AccountSummaryResponse getSummaryByUserId(Long userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
        return new AccountSummaryResponse(account.getId(), account.getCvu(), account.getAlias());
    }
}
