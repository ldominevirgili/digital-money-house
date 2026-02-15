package com.digitalmoneyhouse.account.controller;

import com.digitalmoneyhouse.account.dto.CardRequest;
import com.digitalmoneyhouse.account.dto.CardResponse;
import com.digitalmoneyhouse.account.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Cards")
@RestController
@RequestMapping("/cards")
public class CardsController {

    private final CardService cardService;

    public CardsController(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary = "Crear tarjeta")
    @PostMapping
    public ResponseEntity<CardResponse> create(@Valid @RequestBody CardRequest request, Authentication auth) {
        CardResponse created = cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Listar tarjetas de la cuenta")
    @GetMapping("/accounts/{accountId}/cards")
    public ResponseEntity<List<CardResponse>> listByAccount(@PathVariable Long accountId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(cardService.listByAccount(accountId, userId));
    }

    @Operation(summary = "Detalle de tarjeta")
    @GetMapping("/accounts/{accountId}/cards/{cardId}")
    public ResponseEntity<CardResponse> getOne(@PathVariable Long accountId, @PathVariable Long cardId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(cardService.getByIdAndAccount(accountId, cardId, userId));
    }

    @Operation(summary = "Eliminar tarjeta")
    @DeleteMapping("/accounts/{accountId}/cards/{cardId}")
    public ResponseEntity<Void> delete(@PathVariable Long accountId, @PathVariable Long cardId, Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        cardService.delete(accountId, cardId, userId);
        return ResponseEntity.ok().build();
    }
}
