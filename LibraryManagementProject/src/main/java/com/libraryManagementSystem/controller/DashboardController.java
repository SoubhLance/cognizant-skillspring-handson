package com.libraryManagementSystem.controller;

import com.libraryManagementSystem.dto.ApiResponse;
import com.libraryManagementSystem.dto.DashboardAnalyticsDto;
import com.libraryManagementSystem.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ResponseEntity<ApiResponse<DashboardAnalyticsDto>> getAnalytics() {
        DashboardAnalyticsDto dto = dashboardService.getAnalytics();
        return ResponseEntity.ok(ApiResponse.success(dto, "Dashboard analytics fetched successfully"));
    }
}
