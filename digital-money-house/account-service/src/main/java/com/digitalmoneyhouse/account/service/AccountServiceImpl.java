package com.digitalmoneyhouse.account.service;

import com.digitalmoneyhouse.account.dto.AccountResponse;
import com.digitalmoneyhouse.account.entity.Account;
import com.digitalmoneyhouse.account.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final CvuAliasGenerator cvuAliasGenerator;

    public AccountServiceImpl(AccountRepository accountRepository, CvuAliasGenerator cvuAliasGenerator) {
        this.accountRepository = accountRepository;
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
}
