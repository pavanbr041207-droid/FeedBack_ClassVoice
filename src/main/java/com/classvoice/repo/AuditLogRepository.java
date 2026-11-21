package com.classvoice.repo;

import com.classvoice.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    long countByAction(String action);
}
