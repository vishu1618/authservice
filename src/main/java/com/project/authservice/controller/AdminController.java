package com.project.authservice.controller;

import com.project.authservice.dto.response.ApiResponse;
import com.project.authservice.dto.response.UserResponse;
import com.project.authservice.entity.AuditLog;
import com.project.authservice.service.AuditLogService;
import com.project.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin-only endpoints")
@SecurityRequirement(name = "BearerAuth")
public class AdminController {

    private final UserService userService;
    private final AuditLogService auditLogService;

    @GetMapping("/dashboard")
    @Operation(summary = "Admin dashboard")
    public ResponseEntity<ApiResponse<String>> dashboard() {
        return ResponseEntity.ok(
                ApiResponse.success("Admin dashboard accessible", "Welcome, Administrator!"));
    }

    @GetMapping("/users")
    @Operation(summary = "List all users")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success("Users retrieved", userService.findAll(pageable)));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("User retrieved", userService.findById(id)));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "View all audit logs")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved", auditLogService.findAll(pageable)));
    }

    @GetMapping("/audit-logs/{principal}")
    @Operation(summary = "View audit logs by user")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAuditLogsByPrincipal(
            @PathVariable String principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(
                "Logs for: " + principal, auditLogService.findByPrincipal(principal, pageable)));
    }
}