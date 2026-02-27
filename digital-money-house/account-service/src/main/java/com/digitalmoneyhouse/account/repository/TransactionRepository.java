package com.digitalmoneyhouse.account.repository;

import com.digitalmoneyhouse.account.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    Optional<Transaction> findByIdAndAccountId(Long id, Long accountId);

    List<Transaction> findByAccountIdAndTypeOrderByCreatedAtDesc(Long accountId, String type);
}
