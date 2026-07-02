package com.libraryManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardAnalyticsDto {
    private long totalBooks;
    private long totalUsers;
    private long totalBorrowedBooks;
    private long totalOverdueBooks;
    private BigDecimal totalFinesCollected;
    private List<Map<String, Object>> borrowTrend; // Key: month (String), value: count (Long)
    private List<Map<String, Object>> categoryDistribution; // Key: categoryName, value: count
    private List<AuditLogDto> recentActivities;
}
