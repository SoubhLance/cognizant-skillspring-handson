package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.AuditLogDto;
import com.libraryManagementSystem.entity.AuditLog;
import com.libraryManagementSystem.mapper.AuditLogMapper;
import com.libraryManagementSystem.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLogDto>>> getSystemAuditLogs() {
        List<AuditLogDto> logs = auditLogRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(auditLogMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(logs, "System audit logs fetched successfully"));
    }
}
