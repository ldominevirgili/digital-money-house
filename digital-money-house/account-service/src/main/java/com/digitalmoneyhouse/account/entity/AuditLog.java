package com.digitalmoneyhouse.account.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "action", length = 100)
    private String action;

    @Column(name = "details", length = 1000)
    private String details;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    public AuditLog() {}

    public AuditLog(Long accountId, String action, String details) {
        this.accountId = accountId;
        this.action = action;
        this.details = details;
    }

    public Long getId() { return id; }
    public Long getAccountId() { return accountId; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public Instant getCreatedAt() { return createdAt; }
}
