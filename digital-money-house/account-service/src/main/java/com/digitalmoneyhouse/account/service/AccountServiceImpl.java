package com.digitalmoneyhouse.account.service;

import com.digitalmoneyhouse.account.dto.AccountResponse;
import com.digitalmoneyhouse.account.dto.AccountSummaryResponse;
import com.digitalmoneyhouse.account.dto.AccountUpdateRequest;
import com.digitalmoneyhouse.account.dto.BalanceResponse;
import com.digitalmoneyhouse.account.dto.DepositRequest;
import com.digitalmoneyhouse.account.dto.TransactionResponse;
import com.digitalmoneyhouse.account.entity.Account;
import com.digitalmoneyhouse.account.entity.Transaction;
import com.digitalmoneyhouse.account.exception.ConflictException;
import com.digitalmoneyhouse.account.exception.ForbiddenException;
import com.digitalmoneyhouse.account.exception.ResourceNotFoundException;
import com.digitalmoneyhouse.account.repository.AccountRepository;
import com.digitalmoneyhouse.account.repository.CardRepository;
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
    private final CardRepository cardRepository;
    private final CvuAliasGenerator cvuAliasGenerator;

    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository,
                             CardRepository cardRepository, CvuAliasGenerator cvuAliasGenerator) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
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

    @Override
    @Transactional
    public AccountSummaryResponse updateAlias(Long accountId, Long userId, AccountUpdateRequest request) {
        Account account = getAccountByIdAndUser(accountId, userId);
        if (request.alias() == null || request.alias().isBlank()) {
            return new AccountSummaryResponse(account.getId(), account.getCvu(), account.getAlias());
        }
        String newAlias = request.alias().trim().toLowerCase();
        if (accountRepository.existsByAlias(newAlias) && !newAlias.equals(account.getAlias())) {
            throw new ConflictException("El alias ya esta en uso");
        }
        account.setAlias(newAlias);
        account = accountRepository.save(account);
        return new AccountSummaryResponse(account.getId(), account.getCvu(), account.getAlias());
    }

    @Override
    public List<TransactionResponse> getActivity(Long accountId, Long userId) {
        return getTransactions(accountId, userId);
    }

    @Override
    public TransactionResponse getTransactionDetail(Long accountId, Long transactionId, Long userId) {
        getAccountByIdAndUser(accountId, userId);
        Transaction t = transactionRepository.findByIdAndAccountId(transactionId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaccion no encontrada"));
        return new TransactionResponse(t.getId(), t.getAccountId(), t.getAmount(), t.getType(), t.getDescription(), t.getCreatedAt());
    }

    @Override
    @Transactional
    public TransactionResponse depositFromCard(Long accountId, Long userId, DepositRequest request) {
        Account account = getAccountByIdAndUser(accountId, userId);
        cardRepository.findByIdAndAccountId(request.cardId(), accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada o no pertenece a la cuenta"));
        Transaction tx = new Transaction();
        tx.setAccountId(accountId);
        tx.setAmount(request.amount());
        tx.setType("DEPOSIT");
        tx.setDescription(request.description() != null ? request.description() : "Ingreso desde tarjeta");
        tx = transactionRepository.save(tx);
        account.setBalance(account.getBalance().add(request.amount()));
        accountRepository.save(account);
        return new TransactionResponse(tx.getId(), tx.getAccountId(), tx.getAmount(), tx.getType(), tx.getDescription(), tx.getCreatedAt());
    }
}
