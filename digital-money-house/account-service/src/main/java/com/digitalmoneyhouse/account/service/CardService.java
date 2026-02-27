package com.digitalmoneyhouse.account.service;

import com.digitalmoneyhouse.account.dto.CardRequest;
import com.digitalmoneyhouse.account.dto.CardResponse;
import com.digitalmoneyhouse.account.entity.Card;
import com.digitalmoneyhouse.account.exception.ConflictException;
import com.digitalmoneyhouse.account.exception.ResourceNotFoundException;
import com.digitalmoneyhouse.account.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AccountService accountService;

    public CardService(CardRepository cardRepository, AccountService accountService) {
        this.cardRepository = cardRepository;
        this.accountService = accountService;
    }

    @Transactional
    public CardResponse createCard(CardRequest request) {
        String number = request.number() != null ? request.number().trim() : null;
        if (number == null || number.isEmpty()) {
            throw new ConflictException("Numero de tarjeta invalido");
        }
        if (cardRepository.existsByNumber(number)) {
            throw new ConflictException("La tarjeta ya esta registrada");
        }
        Card card = new Card();
        card.setNumber(number);
        card.setType(request.type());
        card.setHolderName(request.holderName());
        card.setExpiry(request.expiry() != null ? request.expiry() : "");
        card = cardRepository.save(card);
        return toResponse(card);
    }

    @Transactional
    public CardResponse updateCard(Long accountId, Long cardId, Long userId, com.digitalmoneyhouse.account.dto.CardUpdateRequest request) {
        accountService.getAccountByIdAndUser(accountId, userId);
        Card card = cardRepository.findByIdAndAccountId(cardId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada"));
        if (request.holderName() != null) {
            card.setHolderName(request.holderName());
        }
        if (request.expiry() != null) {
            card.setExpiry(request.expiry());
        }
        card = cardRepository.save(card);
        return toResponse(card);
    }

    @Transactional
    public CardResponse associateCardToAccount(Long accountId, Long cardId, Long userId) {
        var account = accountService.getAccountByIdAndUser(accountId, userId);
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada"));
        if (card.getAccountId() != null && !card.getAccountId().equals(accountId)) {
            throw new ConflictException("La tarjeta ya esta asociada a otra cuenta");
        }
        if (cardRepository.existsByAccountIdAndNumber(accountId, card.getNumber())) {
            throw new ConflictException("Esta cuenta ya tiene una tarjeta con ese numero");
        }
        card.setAccountId(accountId);
        card = cardRepository.save(card);
        return toResponse(card);
    }

    public List<CardResponse> listByAccount(Long accountId, Long userId) {
        accountService.getAccountByIdAndUser(accountId, userId);
        return cardRepository.findByAccountId(accountId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public CardResponse getByIdAndAccount(Long accountId, Long cardId, Long userId) {
        accountService.getAccountByIdAndUser(accountId, userId);
        Card card = cardRepository.findByIdAndAccountId(cardId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada"));
        return toResponse(card);
    }

    @Transactional
    public void delete(Long accountId, Long cardId, Long userId) {
        accountService.getAccountByIdAndUser(accountId, userId);
        Card card = cardRepository.findByIdAndAccountId(cardId, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Tarjeta no encontrada"));
        cardRepository.delete(card);
    }

    private CardResponse toResponse(Card c) {
        return new CardResponse(c.getId(), c.getAccountId(), c.getNumber(), c.getType(), c.getHolderName(), c.getExpiry());
    }
}
