package com.digitalmoneyhouse.account.repository;

import com.digitalmoneyhouse.account.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByAccountId(Long accountId);

    Optional<Card> findByIdAndAccountId(Long id, Long accountId);

    boolean existsByNumber(String number);

    boolean existsByAccountIdAndNumber(Long accountId, String number);
}
