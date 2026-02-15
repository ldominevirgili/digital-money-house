package com.digitalmoneyhouse.account.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cards", indexes = {
    @Index(name = "idx_card_account", columnList = "account_id"),
    @Index(name = "idx_card_number", columnList = "number")
})
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(nullable = false, length = 20)
    private String number;

    @Column(nullable = false, length = 20)
    private String type;

    @Column(name = "holder_name", nullable = false, length = 100)
    private String holderName;

    @Column(length = 10)
    private String expiry;

    public Card() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public String getExpiry() { return expiry; }
    public void setExpiry(String expiry) { this.expiry = expiry; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(id, card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
