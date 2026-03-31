package com.project.authservice.repository;

import com.project.authservice.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByPrincipalOrderByCreatedAtDesc(String principal, Pageable pageable);

    Page<AuditLog> findByEventTypeOrderByCreatedAtDesc(String eventType, Pageable pageable);
}