package com.classvoice.service;

import com.classvoice.domain.AuditLog;
import com.classvoice.repo.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    /** Never pass passwords, tokens or OTPs in any argument. */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(Long actorUserId, String action, String entityType, String entityId, String detail) {
        repository.save(new AuditLog(actorUserId, action, entityType, entityId, detail));
    }
}
