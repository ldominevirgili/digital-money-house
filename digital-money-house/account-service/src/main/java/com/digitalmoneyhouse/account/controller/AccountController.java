package com.digitalmoneyhouse.account.controller;

import com.digitalmoneyhouse.account.dto.AccountResponse;
import com.digitalmoneyhouse.account.dto.AccountSummaryResponse;
import com.digitalmoneyhouse.account.dto.AccountUpdateRequest;
import com.digitalmoneyhouse.account.dto.AssociateCardRequest;
import com.digitalmoneyhouse.account.dto.BalanceResponse;
import com.digitalmoneyhouse.account.dto.CardResponse;
import com.digitalmoneyhouse.account.dto.CreateAccountRequest;
import com.digitalmoneyhouse.account.dto.DepositRequest;
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

    @Operation(summary = "Actividad / historial de la cuenta")
    @GetMapping("/{id}/activity")
    public ResponseEntity<List<TransactionResponse>> getActivity(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(accountService.getActivity(id, userId));
    }

    @Operation(summary = "Detalle de una transaccion")
    @GetMapping("/{id}/activity/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionDetail(@PathVariable Long id, @PathVariable Long transactionId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(accountService.getTransactionDetail(id, transactionId, userId));
    }

    @Operation(summary = "Ingresar dinero desde tarjeta")
    @PostMapping("/{id}/transferences")
    public ResponseEntity<TransactionResponse> deposit(@PathVariable Long id, @Valid @RequestBody DepositRequest request, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.depositFromCard(id, userId, request));
    }

    @Operation(summary = "Ultimos destinatarios de transferencias")
    @GetMapping("/{id}/transferences")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public ResponseEntity<java.util.List<com.digitalmoneyhouse.account.dto.TransferRecipientResponse>> getTransferences(@PathVariable Long id, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(accountService.getTransferences(id, userId));
    }

    @Operation(summary = "Realizar transferencia a CVU/Alias")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OK"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad Request"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Not Found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "410", description = "Insufficient Funds"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/{id}/transferences/transfer")
    public ResponseEntity<TransactionResponse> transfer(@PathVariable Long id, @Valid @RequestBody com.digitalmoneyhouse.account.dto.TransferRequest request, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(accountService.transfer(id, userId, request));
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

    @Operation(summary = "Actualizar alias de la cuenta")
    @PatchMapping("/{id}")
    public ResponseEntity<AccountSummaryResponse> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountUpdateRequest request, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(accountService.updateAlias(id, userId, request));
    }
}
