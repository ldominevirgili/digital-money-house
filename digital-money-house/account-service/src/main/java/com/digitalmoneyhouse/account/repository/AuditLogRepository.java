package com.digitalmoneyhouse.account.repository;

import com.digitalmoneyhouse.account.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {}
