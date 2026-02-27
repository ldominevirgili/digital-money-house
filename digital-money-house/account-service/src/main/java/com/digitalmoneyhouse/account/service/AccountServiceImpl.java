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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final CvuAliasGenerator cvuAliasGenerator;
    private final com.digitalmoneyhouse.account.repository.AuditLogRepository auditLogRepository;

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository,
                             CardRepository cardRepository, CvuAliasGenerator cvuAliasGenerator, com.digitalmoneyhouse.account.repository.AuditLogRepository auditLogRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.cardRepository = cardRepository;
        this.cvuAliasGenerator = cvuAliasGenerator;
        this.auditLogRepository = auditLogRepository;
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
    public java.util.List<com.digitalmoneyhouse.account.dto.TransferRecipientResponse> getTransferences(Long accountId, Long userId) {
        getAccountByIdAndUser(accountId, userId);
        log.info("Listing recent transfer recipients for accountId={}", accountId);
        var transfers = transactionRepository.findByAccountIdAndTypeOrderByCreatedAtDesc(accountId, "TRANSFER_OUT");
        java.util.Map<Long, com.digitalmoneyhouse.account.dto.TransferRecipientResponse> byDest = new java.util.LinkedHashMap<>();
        for (Transaction t : transfers) {
            Long destId = t.getCounterpartyAccountId();
            if (destId == null) continue;
            if (byDest.containsKey(destId)) continue;
            var destAcc = accountRepository.findById(destId).orElse(null);
            String cvu = destAcc != null ? destAcc.getCvu() : null;
            String alias = destAcc != null ? destAcc.getAlias() : null;
            byDest.put(destId, new com.digitalmoneyhouse.account.dto.TransferRecipientResponse(destId, cvu, alias, t.getAmount(), t.getCreatedAt()));
            if (byDest.size() >= 10) break;
        }
        var list = java.util.List.copyOf(byDest.values());
        try {
            auditLogRepository.save(new com.digitalmoneyhouse.account.entity.AuditLog(accountId, "LIST_TRANSF_RECIPIENTS", "count=" + list.size()));
        } catch (Exception e) {
            log.warn("Failed to save audit log: {}", e.getMessage());
        }
        return list;
    }

    @Override
    @Transactional
    public TransactionResponse transfer(Long accountId, Long userId, com.digitalmoneyhouse.account.dto.TransferRequest request) {
        log.info("Attempting transfer from accountId={} target={} amount={}", accountId, request.target(), request.amount());
        Account from = getAccountByIdAndUser(accountId, userId);

        Account to = accountRepository.findByCvu(request.target())
                .orElseGet(() -> accountRepository.findByAlias(request.target()).orElse(null));
        if (to == null) {
            throw new ResourceNotFoundException("Cuenta destino no encontrada");
        }
        if (to.getId().equals(from.getId())) {
            throw new IllegalArgumentException("No se puede transferir a la misma cuenta");
        }
        if (from.getBalance().compareTo(request.amount()) < 0) {
            log.warn("Insufficient funds for accountId={} balance={} requested={}", accountId, from.getBalance(), request.amount());
            try { auditLogRepository.save(new com.digitalmoneyhouse.account.entity.AuditLog(accountId, "TRANSFER_FAILED", "insufficient_funds amount=" + request.amount())); } catch (Exception e) { log.warn("Failed to save audit log: {}", e.getMessage()); }
            throw new com.digitalmoneyhouse.account.exception.InsufficientFundsException("Fondos insuficientes");
        }

        Transaction out = new Transaction();
        out.setAccountId(from.getId());
        out.setCounterpartyAccountId(to.getId());
        out.setAmount(request.amount());
        out.setType("TRANSFER_OUT");
        out.setDescription(request.description() != null ? request.description() : "Transferencia saliente");
        out = transactionRepository.save(out);

        Transaction in = new Transaction();
        in.setAccountId(to.getId());
        in.setCounterpartyAccountId(from.getId());
        in.setAmount(request.amount());
        in.setType("TRANSFER_IN");
        in.setDescription(request.description() != null ? request.description() : "Transferencia entrante");
        in = transactionRepository.save(in);

        from.setBalance(from.getBalance().subtract(request.amount()));
        to.setBalance(to.getBalance().add(request.amount()));
        accountRepository.save(from);
        accountRepository.save(to);
        log.info("Transfer successful from accountId={} to accountId={} amount={}", from.getId(), to.getId(), request.amount());
        try { auditLogRepository.save(new com.digitalmoneyhouse.account.entity.AuditLog(from.getId(), "TRANSFER_SUCCESS", "to=" + to.getId() + " amount=" + request.amount())); } catch (Exception e) { log.warn("Failed to save audit log: {}", e.getMessage()); }
        return new TransactionResponse(out.getId(), out.getAccountId(), out.getAmount(), out.getType(), out.getDescription(), out.getCreatedAt());
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
