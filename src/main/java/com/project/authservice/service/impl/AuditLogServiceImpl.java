package com.project.authservice.service.impl;

import com.project.authservice.entity.AuditLog;
import com.project.authservice.repository.AuditLogRepository;
import com.project.authservice.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    public void log(String eventType, String principal, String description,
                    String ipAddress, String outcome) {
        try {
            AuditLog entry = AuditLog.builder()
                    .eventType(eventType)
                    .principal(principal)
                    .description(description)
                    .ipAddress(ipAddress)
                    .outcome(outcome)
                    .build();

            auditLogRepository.save(entry);
        } catch (Exception ex) {
            log.error("Failed to write audit log for [{}] {}: {}",
                    eventType, principal, ex.getMessage());
        }
    }

    @Override
    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Override
    public Page<AuditLog> findByPrincipal(String principal, Pageable pageable) {
        return auditLogRepository.findByPrincipalOrderByCreatedAtDesc(principal, pageable);
    }
}