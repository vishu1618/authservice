package com.project.authservice.service;

import com.project.authservice.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {

    void log(String eventType, String principal, String description,
             String ipAddress, String outcome);

    Page<AuditLog> findAll(Pageable pageable);

    Page<AuditLog> findByPrincipal(String principal, Pageable pageable);
}