package com.digitalmoneyhouse.account.controller;

import com.digitalmoneyhouse.account.dto.AccountResponse;
import com.digitalmoneyhouse.account.dto.AssociateCardRequest;
import com.digitalmoneyhouse.account.dto.BalanceResponse;
import com.digitalmoneyhouse.account.dto.CardResponse;
import com.digitalmoneyhouse.account.dto.CreateAccountRequest;
import com.digitalmoneyhouse.account.dto.AccountSummaryResponse;
import com.digitalmoneyhouse.account.dto.TransactionResponse;
import com.digitalmoneyhouse.account.exception.ForbiddenException;
import com.digitalmoneyhouse.account.service.CardService;
import com.digitalmoneyhouse.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Accounts")
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final CardService cardService;

    public AccountController(AccountService accountService, CardService cardService) {
        this.accountService = accountService;
        this.cardService = cardService;
    }

    @Operation(summary = "Crear cuenta")
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse created = accountService.createAccountForUser(request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Saldo de la cuenta")
    @GetMapping("/{id}")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(accountService.getBalance(id, userId));
    }

    @Operation(summary = "Movimientos de la cuenta")
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(accountService.getTransactions(id, userId));
    }

    @Operation(summary = "Asociar tarjeta a cuenta")
    @PostMapping("/{id}/cards")
    public ResponseEntity<CardResponse> associateCard(@PathVariable Long id, @Valid @RequestBody AssociateCardRequest request, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.associateCardToAccount(id, request.cardId(), userId));
    }

    @Operation(summary = "Resumen cuenta por usuario")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<AccountSummaryResponse> getByUser(@PathVariable Long userId, Authentication auth) {
        Long authUserId = (Long) auth.getPrincipal();
        if (!authUserId.equals(userId)) {
            throw new ForbiddenException("Sin permisos");
        }
        return ResponseEntity.ok(accountService.getSummaryByUserId(userId));
    }
}
