package com.digitalmoneyhouse.account;

import com.digitalmoneyhouse.account.dto.AccountResponse;
import com.digitalmoneyhouse.account.repository.AccountRepository;
import com.digitalmoneyhouse.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AccountServiceTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void createAccountForUser_generatesCvuAndAlias() {
        Long userId = 999L;
        AccountResponse response = accountService.createAccountForUser(userId);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.cvu()).hasSize(22);
        assertThat(response.cvu()).matches("\\d{22}");
        assertThat(response.alias()).matches("[a-z]+\\.[a-z]+\\.[a-z]+");
        assertThat(response.balance()).isZero();
    }
}
