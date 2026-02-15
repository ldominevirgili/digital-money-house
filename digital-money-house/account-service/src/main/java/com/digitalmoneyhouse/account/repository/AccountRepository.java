package com.digitalmoneyhouse.account.repository;

import com.digitalmoneyhouse.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUserId(Long userId);

    boolean existsByCvu(String cvu);

    boolean existsByAlias(String alias);
}
